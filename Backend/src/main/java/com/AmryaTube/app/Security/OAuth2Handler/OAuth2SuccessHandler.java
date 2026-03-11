package com.AmryaTube.app.Security.OAuth2Handler;

import com.AmryaTube.app.Entity.User;
import com.AmryaTube.app.Enums.AuthProvider;
import com.AmryaTube.app.Enums.GlobalRole;
import com.AmryaTube.app.Repository.UserRepository;
import com.AmryaTube.app.Service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        if (oAuth2User == null) {
            throw new OAuth2AuthenticationException("No OAuth2 user details found");
        }

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");
        String pictureUrl = oAuth2User.getAttribute("picture");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not provided by OAuth2 provider");
        }

        try {
            userRepository.findByEmail(email)
                    .map(existUser -> handleExistUser(existUser, providerId, pictureUrl))
                    .orElseGet(() -> handleNewUser(email, name, providerId, pictureUrl));

            String accessToken = jwtService.generateJwtToken(email, GlobalRole.VIEWER.toString());

            // ✅ FIX: Use ResponseCookie instead of Cookie.toString()
            // Cookie.toString() prints "jakarta.servlet.http.Cookie@3a5b1c2e"
            // ResponseCookie.toString() prints "access-token=eyJ...; Path=/; HttpOnly; Secure; SameSite=Strict"
            ResponseCookie accessCookie = ResponseCookie.from("access-token", accessToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(30 * 24 * 60 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

            response.sendRedirect("/api/user/hello");

        } catch (Exception e) {
            throw new OAuth2AuthenticationException("Error while saving user: " + e.getMessage());
        }
    }

    private User handleExistUser(User user, String providerId, String pictureUrl) {
        boolean needsUpdate = false;
        if (user.getProviderId() == null) {
            user.setProviderId(providerId);
            needsUpdate = true;
        }
        if (user.getPictureUrl() == null) {
            user.setPictureUrl(pictureUrl);
            needsUpdate = true;
        }
        if (needsUpdate) {
            return userRepository.save(user);
        }
        return user;
    }

    private User handleNewUser(String email, String name, String providerId, String pictureUrl) {
        return userRepository.save(User.builder()
                .email(email)
                .name(name)
                .providerId(providerId)
                .pictureUrl(pictureUrl)
                .authProvider(AuthProvider.GOOGLE)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .build());
    }
}