package com.AmryaTube.app.Security;

import com.AmryaTube.app.Security.OAuth2Handler.OAuth2FailureHandler;
import com.AmryaTube.app.Security.OAuth2Handler.OAuth2SuccessHandler;
import com.AmryaTube.app.Security.SecurityService.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtFilter jwtFilter,
                          OAuth2SuccessHandler oAuth2SuccessHandler,
                          OAuth2FailureHandler oAuth2FailureHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtFilter = jwtFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                // ✅ THIS IS THE KEY FIX
                // Without this, Spring redirects unauthenticated API requests to Google login
                // With this, it returns 401 Unauthorized instead
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // If the request is for the OAuth2 login flow, let it redirect
                            String requestUri = request.getRequestURI();
                            if (requestUri.startsWith("/oauth2") || requestUri.startsWith("/login/oauth2")) {
                                response.sendRedirect("/oauth2/authorization/google");
                            } else {
                                // For API requests, return 401 instead of redirecting
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.getWriter().write(
                                        "{\"error\": \"Unauthorized\", \"message\": \"You must be logged in to access this resource\"}"
                                );
                            }
                        })
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}