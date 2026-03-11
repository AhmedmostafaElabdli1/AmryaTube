package com.AmryaTube.app.Controller;

import com.AmryaTube.app.DTOs.Request.LoginRequest;
import com.AmryaTube.app.DTOs.Request.RegisterRequest;
import com.AmryaTube.app.Service.AuthService;
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
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult result, HttpServletResponse response) {
        log.info("Registering user");
        if (result.hasErrors()) {
            String errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Unknown error");
            log.error(errors);
            return ResponseEntity.badRequest().body(errors);
        }
        return authService.registerUser(request, response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.loginUser(request, response);
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // ✅ Now passing request so we can invalidate the session
        return authService.logoutUser(request, response);
    }
}