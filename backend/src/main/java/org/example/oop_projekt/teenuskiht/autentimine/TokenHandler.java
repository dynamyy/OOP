package org.example.oop_projekt.teenuskiht.autentimine;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenHandler {
    private static Key key;

    // Token kehtib 15 min
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 100;

    public TokenHandler(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }


    public static String genereeriToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuer("ostukorvivordlus")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(key)
                .compact();
    }
}

