package com.AmryaTube.app.playlist.exception;

import java.util.UUID;

public class VideoAlreadyInPlaylist extends RuntimeException {
    public VideoAlreadyInPlaylist(UUID videoId, UUID playlistId) {
        super("Video '" + videoId + "' is already in playlist '" + playlistId + "'.");
    }
}
