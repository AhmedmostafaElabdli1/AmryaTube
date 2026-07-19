package com.AmryaTube.app.auth.controller;

import com.AmryaTube.app.auth.dto.request.LoginRequest;
import com.AmryaTube.app.auth.dto.request.RegisterRequest;
import com.AmryaTube.app.auth.dto.response.AuthResponse;
import com.AmryaTube.app.auth.service.AuthService;
import com.AmryaTube.app.common.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private static final int THIRTY_DAYS_SECONDS = 30 * 24 * 60 * 60;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                              HttpServletResponse response) {
        ApiResponse<AuthResponse> body = authService.registerUser(request);
        addAccessCookie(response, body.getData().getToken(), THIRTY_DAYS_SECONDS);
        return body;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                           HttpServletResponse response) {
        ApiResponse<AuthResponse> body = authService.loginUser(request);
        int maxAge = request.isRememberMe() ? THIRTY_DAYS_SECONDS : -1;
        addAccessCookie(response, body.getData().getToken(), maxAge);
        return body;
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutUser();

        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        addAccessCookie(response, "", 0);
        return ApiResponse.of(HttpStatus.OK, "Logout successful");
    }

    private void addAccessCookie(HttpServletResponse response, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from("access-token", value)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
