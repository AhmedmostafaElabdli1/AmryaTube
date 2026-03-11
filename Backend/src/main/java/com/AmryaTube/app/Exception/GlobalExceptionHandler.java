package com.AmryaTube.app.Exception;


import com.AmryaTube.app.Exception.CustomUserException.EmailAlreadyRegistered;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.AmryaTube.app.DTOs.Response.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;


import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse errorResponseBuilder(HttpStatus status,String error, String message, HttpServletRequest request){

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
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            HttpServletRequest request,
            AccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponseBuilder(
                        HttpStatus.FORBIDDEN,
                        "Access Denied",
                        "You don't have permission to access this resource",
                        request
                ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            HttpServletRequest request,
            Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseBuilder(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        "An unexpected error occurred",
                        request
                ));
    }





    @ExceptionHandler(EmailAlreadyRegistered.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyRegistered(HttpServletRequest request, EmailAlreadyRegistered exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseBuilder(
                        HttpStatus.BAD_REQUEST,
                        "Email Already Registered",
                        exception.getMessage(),
                        request
                ));
    }

}
