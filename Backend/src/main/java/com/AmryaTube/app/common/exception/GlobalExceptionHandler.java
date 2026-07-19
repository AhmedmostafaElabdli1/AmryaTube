package com.AmryaTube.app.common.exception;

import com.AmryaTube.app.auth.exception.OrganizationRegisterationNotAllowed;
import com.AmryaTube.app.common.dto.response.ErrorResponse;
import com.AmryaTube.app.playlist.exception.PlaylistNotFound;
import com.AmryaTube.app.playlist.exception.VideoAlreadyInPlaylist;
import com.AmryaTube.app.user.exception.EmailAlreadyRegistered;
import com.AmryaTube.app.user.exception.UsernameAlreadyRegistered;
import com.AmryaTube.app.user.exception.UserNotExist;
import com.AmryaTube.app.video.exception.VideoNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

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
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(HttpServletRequest req, AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Access Denied", "You don't have permission to access this resource", req);
    }

    @ExceptionHandler(OrganizationRegisterationNotAllowed.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOrganizationRegistrationNotAllowed(HttpServletRequest req, OrganizationRegisterationNotAllowed ex) {
        return build(HttpStatus.FORBIDDEN, "Organization Registration Not Allowed", ex.getMessage(), req);
    }

    @ExceptionHandler(EmailAlreadyRegistered.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExists(HttpServletRequest req, EmailAlreadyRegistered ex) {
        return build(HttpStatus.CONFLICT, "Email Already Registered", ex.getMessage(), req);
    }

    @ExceptionHandler(UsernameAlreadyRegistered.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUsernameExists(HttpServletRequest req, UsernameAlreadyRegistered ex) {
        return build(HttpStatus.CONFLICT, "Username Already Registered", ex.getMessage(), req);
    }

    @ExceptionHandler(UserNotExist.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(HttpServletRequest req, UserNotExist ex) {
        return build(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler({PlaylistNotFound.class, VideoNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(HttpServletRequest req, RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(VideoAlreadyInPlaylist.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleVideoAlreadyInPlaylist(HttpServletRequest req, VideoAlreadyInPlaylist ex) {
        return build(HttpStatus.CONFLICT, "Video Already In Playlist", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(HttpServletRequest req, MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Validation Failed", message, req);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(HttpServletRequest req, Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", req);
    }
}