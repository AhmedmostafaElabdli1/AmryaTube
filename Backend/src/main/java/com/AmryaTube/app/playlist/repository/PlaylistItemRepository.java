package com.AmryaTube.app.playlist.repository;

import com.AmryaTube.app.playlist.entity.Playlist;
import com.AmryaTube.app.playlist.entity.PlaylistItem;
import com.AmryaTube.app.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, UUID> {

    // Paged read for the API (handles large playlists without loading everything)
    Page<PlaylistItem> findByPlaylistOrderByPositionAsc(Playlist playlist, Pageable pageable);

    // Full ordered list for internal reindexing on remove/reorder
    List<PlaylistItem> findByPlaylistOrderByPositionAsc(Playlist playlist);

    Optional<PlaylistItem> findByPlaylistAndVideo(Playlist playlist, Video video);

    boolean existsByPlaylistAndVideo(Playlist playlist, Video video);

    long countByPlaylist(Playlist playlist);
}
