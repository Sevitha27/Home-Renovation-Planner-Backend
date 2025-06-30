package com.lowes.service;

import com.lowes.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Base64;

@Service
public class JwtService {

    private final String SECRET_KEY = "245fb9c673882102dd60539ba41cf422b08445527313547c03aa9dc85c05a652e386b486bea65fce6f813dee12df7bc8bc6017ef9b219b0018e9c1d2e457feeb730d543adc587851e5c9bcf67322cbcbbf73a1e9ea6cccf673ffe1dbc0dfb16036415b8d5e3e911de28a318be0fc297b2c6279839bd7f25b4cfbcd8e62a2185ea9e46a4efea874e578cd2b4355eaf66d3049c8ef1a68ca47bd20ad9eef62625085ba0547595fe6a8ce340e628ba98c756cace30536a81e1107e4d7c24a9fccf52b6c71160314bfe2828d33b5c8a9506e4b45365a3a2e09d11c9d23a9d8f00323dfa30d0230d26a4403eb35d782d40241794aedef69258e53874dc6ac887d9378";

    private SecretKey getSignKey(){
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateAccessToken(User user){
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("type", "access")
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("type", "refresh")
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //validateToken
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    //extractEmail
    public String extractEmail(String token){ //access or refresh token
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getClaims(token).get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getClaims(token).get("type", String.class));
    }

    //getClaims
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}