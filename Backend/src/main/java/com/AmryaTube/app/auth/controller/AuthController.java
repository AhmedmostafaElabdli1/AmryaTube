package com.AmryaTube.app.auth.controller;

import com.AmryaTube.app.auth.dto.request.LoginRequest;
import com.AmryaTube.app.auth.dto.request.RegisterRequest;
import com.AmryaTube.app.auth.dto.response.AuthResponse;
import com.AmryaTube.app.auth.service.AuthService;
import com.AmryaTube.app.common.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                              HttpServletResponse response) {
        AuthResponse data = authService.registerUser(request, response);
        return ApiResponse.created(data, "User registered successfully");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                           HttpServletResponse response) {
        AuthResponse data = authService.loginUser(request, response);
        return ApiResponse.ok(data, "Login successful");
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutUser(request, response);
        return ApiResponse.of(HttpStatus.OK, "Logout successful");
    }
}
