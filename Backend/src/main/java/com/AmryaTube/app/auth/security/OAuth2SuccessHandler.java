package com.AmryaTube.app.auth.security;

import com.AmryaTube.app.user.entity.User;
import com.AmryaTube.app.common.enums.AuthProvider;
import com.AmryaTube.app.common.enums.GlobalRole;
import com.AmryaTube.app.user.repository.UserRepository;
import com.AmryaTube.app.auth.service.JwtService;
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
            User user = userRepository.findByEmail(email)
                    .map(existing -> handleExistingUser(existing, providerId, pictureUrl))
                    .orElseGet(() -> handleNewUser(email, name, providerId, pictureUrl));

            String accessToken = jwtService.generateJwtToken(email, user.getRole().toString());

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

    private User handleExistingUser(User user, String providerId, String pictureUrl) {
        boolean needsUpdate = false;
        if (user.getProviderId() == null) {
            user.setProviderId(providerId);
            needsUpdate = true;
        }
        if (user.getPictureUrl() == null) {
            user.setPictureUrl(pictureUrl);
            needsUpdate = true;
        }
        return needsUpdate ? userRepository.save(user) : user;
    }

    private User handleNewUser(String email, String name, String providerId, String pictureUrl) {
        return userRepository.save(User.builder()
                .email(email)
                .name(name)
                .providerId(providerId)
                .pictureUrl(pictureUrl)
                .authProvider(AuthProvider.GOOGLE)
                .role(GlobalRole.VIEWER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .build());
    }
}
