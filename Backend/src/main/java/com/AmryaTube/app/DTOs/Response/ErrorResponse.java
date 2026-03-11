package com.AmryaTube.app.DTOs.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private boolean success;   // always false
    private int status;        // HTTP status code
    private String error;      // short error type
    private String message;    // user-friendly message
    private String path;       // request path
    private Instant timestamp;

}
