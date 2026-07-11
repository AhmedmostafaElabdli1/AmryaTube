package com.AmryaTube.app.video.exception;

import java.util.UUID;

public class VideoNotFound extends RuntimeException {
    public VideoNotFound(UUID id) {
        super("Video with id '" + id + "' does not exist.");
    }
}
