package com.AmryaTube.app.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private int status;
    private String message;
    private T data;
    private Instant timestamp;

    public static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(status.is2xxSuccessful())
                .status(status.value())
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> of(HttpStatus status, String message) {
        return of(status, message, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, "Success", data);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return of(HttpStatus.OK, message, data);
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return of(HttpStatus.CREATED, message, data);
    }

    public static <T> ApiResponse<T> deleted(String message) {
        return of(HttpStatus.OK, message);
    }
}