package com.AmryaTube.app.auth.service;

import com.AmryaTube.app.auth.dto.request.LoginRequest;
import com.AmryaTube.app.auth.dto.request.RegisterRequest;
import com.AmryaTube.app.auth.dto.response.AuthResponse;
import com.AmryaTube.app.common.dto.response.ApiResponse;
import com.AmryaTube.app.common.enums.AuthProvider;
import com.AmryaTube.app.common.enums.GlobalRole;
import com.AmryaTube.app.user.entity.User;
import com.AmryaTube.app.user.exception.EmailAlreadyRegistered;
import com.AmryaTube.app.user.exception.UserNotExist;
import com.AmryaTube.app.user.exception.UsernameAlreadyRegistered;
import com.AmryaTube.app.user.repository.UserRepository;
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

    public ApiResponse<AuthResponse> registerUser(RegisterRequest request) {
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

        String token = jwtService.generateJwtToken(request.getEmail(), user.getRole().toString());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())));
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ApiResponse.created(toAuthResponse(user, token), "User registered successfully");
    }

    public ApiResponse<AuthResponse> loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotExist(request.getEmail()));

        String token = jwtService.generateJwtToken(request.getEmail(), user.getRole().toString());

        return ApiResponse.ok(toAuthResponse(user, token), "Login successful");
    }

    public void logoutUser() {
        SecurityContextHolder.clearContext();
    }

    private AuthResponse toAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }
}
