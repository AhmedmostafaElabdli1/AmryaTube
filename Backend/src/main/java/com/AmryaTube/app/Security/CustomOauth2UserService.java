package com.AmryaTube.app.Security;

import com.AmryaTube.app.auth.exception.OrganizationRegisterationNotAllowed;
import com.AmryaTube.app.user.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email != null && email.endsWith("@gmail.com")) {
            // blocks personal Gmail accounts — only org/custom-domain Google accounts allowed
            throw new OrganizationRegisterationNotAllowed("@gmail.com");
        }

        return oAuth2User;
    }
}
