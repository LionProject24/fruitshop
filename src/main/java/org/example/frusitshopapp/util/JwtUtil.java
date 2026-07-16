package org.example.frusitshopapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION = 1000 * 60 * 60 * 24;
    private final ConcurrentHashMap<String,Boolean> invalidTokens = new ConcurrentHashMap<>();


    private Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

//    1. 토큰생성
    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION))
                .signWith(key)
                .compact();
    }

//    2. 토큰에서 username 꺼내기
    public String getUsername(String token){
        return getClaims(token).getSubject();
    }

//    3. 토큰 검증
    public boolean validateToken(String token){
        if (invalidTokens.containsKey(token))
            return false;
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    4. 로그아웃(토큰 무효화)
    public void invalidateToken(String token){
        invalidTokens.put(token,true);
    }
}
