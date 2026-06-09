package com.myProjects.messagingApp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key SECRET_KEY = Keys.hmacShaKeyFor("random-secret-key-for-application-chat-app".getBytes());

    public String generateJwtToken(String username) {

        Map<String, Object> claims = new HashMap<>();
        return createJwtToken(username, claims);

    }

    private String createJwtToken(String subject, Map<String, Object> claims) {

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims extractAllClaims(String jwtToken) {

        return Jwts.parser()
                .verifyWith((SecretKey) SECRET_KEY)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public boolean isTokenExpired(Date expirationDate) {

        return expirationDate.before(new Date(System.currentTimeMillis()));

    }

}
