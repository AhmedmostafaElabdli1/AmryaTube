package com.AmryaTube.app.playlist.exception;

import java.util.UUID;

public class PlaylistNotFound extends RuntimeException {
    public PlaylistNotFound(UUID id) {
        super("Playlist with id '" + id + "' does not exist.");
    }
}
