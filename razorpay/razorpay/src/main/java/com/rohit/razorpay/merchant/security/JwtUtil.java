package com.rohit.razorpay.merchant.security;

import com.rohit.razorpay.common.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret_key}")
    private String secret;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String email, UUID merchantId, UserRole role){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("merchant_id",merchantId)
                .claim("role",role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(60*10)))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims verifyAccessToken(String accessToken){
        //returns claims like merchant_id, role
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
    }
}
