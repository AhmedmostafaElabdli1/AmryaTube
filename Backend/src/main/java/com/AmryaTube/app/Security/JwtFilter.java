package com.AmryaTube.app.security;

import com.AmryaTube.app.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }



    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response , FilterChain filterChain)
    throws ServletException, IOException {

        String token = resolveToken(request);

        if(token == null){
            log.debug("No token found in request");
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(token)) {
                log.debug("Token is valid");
                String email = jwtService.extractEmail(token);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email,
                                null,
                                List.of(jwtService.extractRole(token)
                                )
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        }

        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("Token from Authorization header: {}", token);
            return token;
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("access-token")) {
                    log.debug("Token from access-token cookie: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}
