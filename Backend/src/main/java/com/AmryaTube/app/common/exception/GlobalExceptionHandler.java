package com.AmryaTube.app.common.exception;

import com.AmryaTube.app.common.dto.response.ErrorResponse;
import com.AmryaTube.app.playlist.exception.PlaylistNotFound;
import com.AmryaTube.app.playlist.exception.VideoAlreadyInPlaylist;
import com.AmryaTube.app.user.exception.EmailAlreadyRegistered;
import com.AmryaTube.app.user.exception.UsernameAlreadyRegistered;
import com.AmryaTube.app.user.exception.UserNotExist;
import com.AmryaTube.app.video.exception.VideoNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse build(HttpStatus status, String error, String message, HttpServletRequest request) {
        return ErrorResponse.builder()
                .success(false)
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(HttpServletRequest req, AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(build(HttpStatus.FORBIDDEN, "Access Denied", "You don't have permission to access this resource", req));
    }

    @ExceptionHandler(EmailAlreadyRegistered.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(HttpServletRequest req, EmailAlreadyRegistered ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT, "Email Already Registered", ex.getMessage(), req));
    }

    @ExceptionHandler(UsernameAlreadyRegistered.class)
    public ResponseEntity<ErrorResponse> handleUsernameExists(HttpServletRequest req, UsernameAlreadyRegistered ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT, "Username Already Registered", ex.getMessage(), req));
    }

    @ExceptionHandler(UserNotExist.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(HttpServletRequest req, UserNotExist ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(build(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), req));
    }

    @ExceptionHandler({PlaylistNotFound.class, VideoNotFound.class})
    public ResponseEntity<ErrorResponse> handleNotFound(HttpServletRequest req, RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req));
    }

    @ExceptionHandler(VideoAlreadyInPlaylist.class)
    public ResponseEntity<ErrorResponse> handleVideoAlreadyInPlaylist(HttpServletRequest req, VideoAlreadyInPlaylist ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT, "Video Already In Playlist", ex.getMessage(), req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(HttpServletRequest req, Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", req));
    }
}
