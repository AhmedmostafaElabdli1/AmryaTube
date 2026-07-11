package com.AmryaTube.app.auth.controller;

import com.AmryaTube.app.auth.dto.request.LoginRequest;
import com.AmryaTube.app.auth.dto.request.RegisterRequest;
import com.AmryaTube.app.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      BindingResult result,
                                      HttpServletResponse response) {
        if (result.hasErrors()) {
            String errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Validation error");
            log.warn("Register validation failed: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        return authService.registerUser(request, response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletResponse response) {
        return authService.loginUser(request, response);
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logoutUser(request, response);
    }
}
