package com.AmryaTube.app.playlist.service;

import com.AmryaTube.app.playlist.entity.Playlist;
import com.AmryaTube.app.playlist.entity.PlaylistItem;
import com.AmryaTube.app.playlist.exception.PlaylistNotFound;
import com.AmryaTube.app.playlist.exception.VideoAlreadyInPlaylist;
import com.AmryaTube.app.playlist.repository.PlaylistItemRepository;
import com.AmryaTube.app.playlist.repository.PlaylistRepository;
import com.AmryaTube.app.video.entity.Video;
import com.AmryaTube.app.video.exception.VideoNotFound;
import com.AmryaTube.app.video.repository.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final VideoRepository videoRepository;

    public PlaylistService(PlaylistRepository playlistRepository,
                           PlaylistItemRepository playlistItemRepository,
                           VideoRepository videoRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistItemRepository = playlistItemRepository;
        this.videoRepository = videoRepository;
    }

    /**
     * Appends a video to the end of a playlist.
     * NOTE: ownership/authorization (does the current user own this playlist?)
     * should be enforced by the controller or a security check before calling this.
     */
    @Transactional
    public PlaylistItem addVideo(UUID playlistId, UUID videoId) {
        Playlist playlist = getPlaylistOrThrow(playlistId);
        Video video = getVideoOrThrow(videoId);

        if (playlistItemRepository.existsByPlaylistAndVideo(playlist, video)) {
            throw new VideoAlreadyInPlaylist(videoId, playlistId);
        }

        int nextPosition = (int) playlistItemRepository.countByPlaylist(playlist);

        PlaylistItem item = PlaylistItem.builder()
                .playlist(playlist)
                .video(video)
                .position(nextPosition)
                .build();

        return playlistItemRepository.save(item);
    }

    /**
     * Removes a video from a playlist and closes the gap so positions stay
     * contiguous (0, 1, 2, ...).
     */
    @Transactional
    public void removeVideo(UUID playlistId, UUID videoId) {
        Playlist playlist = getPlaylistOrThrow(playlistId);
        Video video = getVideoOrThrow(videoId);

        PlaylistItem item = playlistItemRepository.findByPlaylistAndVideo(playlist, video)
                .orElseThrow(() -> new VideoNotFound(videoId));

        playlistItemRepository.delete(item);
        reindex(playlist);
    }

    /**
     * Moves a video to a new 0-based position within the playlist, shifting the
     * other videos to keep ordering contiguous. newPosition is clamped to the
     * valid range.
     */
    @Transactional
    public void reorderVideo(UUID playlistId, UUID videoId, int newPosition) {
        Playlist playlist = getPlaylistOrThrow(playlistId);
        Video video = getVideoOrThrow(videoId);

        List<PlaylistItem> items = playlistItemRepository.findByPlaylistOrderByPositionAsc(playlist);

        PlaylistItem target = items.stream()
                .filter(i -> i.getVideo().getId().equals(video.getId()))
                .findFirst()
                .orElseThrow(() -> new VideoNotFound(videoId));

        int clamped = Math.max(0, Math.min(newPosition, items.size() - 1));

        items.remove(target);
        items.add(clamped, target);

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPosition(i);
        }
        playlistItemRepository.saveAll(items);
    }

    @Transactional(readOnly = true)
    public Page<PlaylistItem> getItems(UUID playlistId, Pageable pageable) {
        Playlist playlist = getPlaylistOrThrow(playlistId);
        return playlistItemRepository.findByPlaylistOrderByPositionAsc(playlist, pageable);
    }

    /** Reassigns contiguous 0-based positions to every item in the playlist. */
    private void reindex(Playlist playlist) {
        List<PlaylistItem> items = playlistItemRepository.findByPlaylistOrderByPositionAsc(playlist);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPosition(i);
        }
        playlistItemRepository.saveAll(items);
    }

    private Playlist getPlaylistOrThrow(UUID playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFound(playlistId));
    }

    private Video getVideoOrThrow(UUID videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoNotFound(videoId));
    }
}
