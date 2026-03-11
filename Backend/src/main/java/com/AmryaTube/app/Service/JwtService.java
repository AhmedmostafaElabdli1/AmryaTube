package com.AmryaTube.app.Service;


import ch.qos.logback.classic.net.SocketReceiver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value( "${jwt.secret}")
    private String jwtSecret;


    private Key getSignKey(String secret){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(String email, String role ){

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_"+role);

        // 15 mins
        int jwtExpirationInMs = 1000 * 60 * 15;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .signWith(getSignKey(jwtSecret) , SignatureAlgorithm.HS256 )
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .compact();
    }

    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey(jwtSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token){
        return extractAllClaims(token)
                .getSubject();
    }

    public SimpleGrantedAuthority extractRole(String token){
        return new SimpleGrantedAuthority(extractAllClaims(token)
                .get("role", String.class));
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token){
        return !isTokenExpired(token);
    }










}
