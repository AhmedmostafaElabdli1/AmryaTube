package com.AmryaTube.app.Security.SecurityService;

import com.AmryaTube.app.Exception.AuthException.OrganizationRegisterationNotAllowed;
import com.AmryaTube.app.Repository.UserRepository;
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
        // send to google endpoint to get User Info
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // must be check here because if it fail then Auth'll fail
        String email = oAuth2User.getAttribute("email");
        if(email.endsWith("@gmail.com")){

            throw new OrganizationRegisterationNotAllowed("@gmail.com");
             //this throw exception if user is organization email
            // and it'll be handled in AuthFailHandler because this not reaches to Global handler
            // Because this happens inside the OAuth2 / Spring Security flow,
            // the request never reaches a controller
            // So GlobalExceptionHandler is NOT used
            // The exception is handled by AuthenticationFailureHandler (OAuth2 failure handler)

        }

        return oAuth2User;



    }
}
