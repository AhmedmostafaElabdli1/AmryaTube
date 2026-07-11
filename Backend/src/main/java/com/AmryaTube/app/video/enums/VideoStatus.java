package com.AmryaTube.app.video.enums;

public enum VideoStatus {
    UPLOADING,   // presigned URL issued, frontend uploading to MinIO
    PROCESSING,  // uploaded, being transcoded/thumbnailed
    PUBLISHED,   // ready to watch
    PRIVATE,     // hidden by owner
    UNLISTED,    // only accessible via direct link
    DELETED      // soft deleted
}
