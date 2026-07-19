package com.AmryaTube.app.auth.service;

import com.AmryaTube.app.auth.dto.request.LoginRequest;
import com.AmryaTube.app.auth.dto.request.RegisterRequest;
import com.AmryaTube.app.auth.dto.response.AuthResponse;
import com.AmryaTube.app.common.enums.AuthProvider;
import com.AmryaTube.app.common.enums.GlobalRole;
import com.AmryaTube.app.user.entity.User;
import com.AmryaTube.app.user.exception.EmailAlreadyRegistered;
import com.AmryaTube.app.user.exception.UserNotExist;
import com.AmryaTube.app.user.exception.UsernameAlreadyRegistered;
import com.AmryaTube.app.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse registerUser(RegisterRequest request, HttpServletResponse response) {
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
                .role(GlobalRole.VIEWER)
                .isAccountNonExpired(true)
                .isEnabled(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateJwtToken(request.getEmail(), GlobalRole.VIEWER.toString());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_" + GlobalRole.VIEWER)));
        SecurityContextHolder.getContext().setAuthentication(auth);

        setAccessCookie(response, token, 30 * 24 * 60 * 60);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

    public AuthResponse loginUser(LoginRequest request, HttpServletResponse response) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new UserNotExist(request.getEmail());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotExist(request.getEmail()));

        String token = jwtService.generateJwtToken(request.getEmail(), GlobalRole.VIEWER.toString());
        int maxAge = request.isRememberMe() ? 30 * 24 * 60 * 60 : -1;
        setAccessCookie(response, token, maxAge);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        setAccessCookie(response, "", 0);

        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        SecurityContextHolder.clearContext();
    }

    private void setAccessCookie(HttpServletResponse response, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from("access-token", value)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
