package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt_key_1}")
    private String secretKey;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User currentUser) {

        Map<String, String> customClaims = new HashMap<>();
        customClaims.putIfAbsent("Id",currentUser.getId().toString());

        return Jwts.builder()
                .claims()
                .add(customClaims)
                .subject(currentUser.getEmailId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 2*60*60*1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims allClaims = extractClaims(token);
        return claimResolver.apply(allClaims);
    }

    public boolean isTokenValid(String token, User currentUser){
        String currentUserEmail = extractEmail(token);
        return currentUserEmail.equalsIgnoreCase(currentUser.getEmailId()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date tokenExpiration = extractClaim(token, Claims::getExpiration);
        return tokenExpiration.before(new Date());
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
