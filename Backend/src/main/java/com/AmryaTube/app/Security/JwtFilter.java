package com.AmryaTube.app.security;

import com.AmryaTube.app.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }



    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response , FilterChain filterChain)
    throws ServletException, IOException {

        String token=null;
        String authHeader = request.getHeader("Authorization");


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.debug("Token is here "+ token);
        }else{
            log.info("Token is null");
        }

        if(token == null && request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("access-token")){
                    log.info("Access Token is here "+ cookie.getValue());
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null){
            log.debug("Token is null");
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(token)) {
                System.out.println("Token is valid");
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

}
