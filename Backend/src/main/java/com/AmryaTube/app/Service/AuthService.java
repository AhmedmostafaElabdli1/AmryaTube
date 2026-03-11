package com.AmryaTube.app.Service;

import com.AmryaTube.app.DTOs.Request.LoginRequest;
import com.AmryaTube.app.DTOs.Request.RegisterRequest;
import com.AmryaTube.app.Entity.User;
import com.AmryaTube.app.Enums.AuthProvider;
import com.AmryaTube.app.Enums.GlobalRole;
import com.AmryaTube.app.Exception.CustomUserException.EmailAlreadyRegistered;
import com.AmryaTube.app.Exception.CustomUserException.UserNotExist;
import com.AmryaTube.app.Exception.CustomUserException.UsernameAlreadyRegistered;
import com.AmryaTube.app.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> registerUser(RegisterRequest request, HttpServletResponse response) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyRegistered(request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyRegistered(request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authProvider(AuthProvider.LOCAL)
                .isAccountNonExpired(true)
                .isEnabled(true)
                .build();

        userRepository.save(user);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + GlobalRole.VIEWER)));

        int maxAge = 30 * 24 * 60 * 60;

        String token = jwtService.generateJwtToken(request.getEmail(), GlobalRole.VIEWER.toString());

        ResponseCookie accessCookie = ResponseCookie.from("access-token", token)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return ResponseEntity.ok()
                .body("User Registered Successfully\n Your Token is: " + token);
    }

    public ResponseEntity<?> loginUser(LoginRequest request, HttpServletResponse response) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new UserNotExist(request.getEmail());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        int maxAge = request.isRememberMe() ? 30 * 24 * 60 * 60 : -1;

        String token = jwtService.generateJwtToken(request.getEmail(), GlobalRole.VIEWER.toString());

        ResponseCookie accessCookie = ResponseCookie.from("access-token", token)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        return ResponseEntity.ok()
                .body("Login Successful JWT Token: " + token);
    }

    // ✅ Changed: now accepts HttpServletRequest too
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {

        // 1. Clear the cookie
        ResponseCookie accessCookie = ResponseCookie.from("access-token", "")
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        // 2. Invalidate the session (OAuth2 creates one during login)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 3. Clear SecurityContext
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().body("Logout Successful");
    }
}