package com.lionproject24.fruitshop.util;

import com.lionproject24.fruitshop.entity.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenizer {

// [Token 초기값 선언]
    // Access token - 클라 Api 호출 시 서버로 보냄 | Refresh token -access token 만료시 서버로 재발급 요청

    // Access Token 서명할 때 쓸 비밀키 (byte)
    private final byte[] accessSecret;
    // refresh Token 서명할 때 쓸 비밀키 (byte)
    private final byte[] refreshSecret;
    // Access Token 몇 밀리초 뒤에 만료되는 지
    private final long accessExpirationMs;
    // refresh Token 몇 밀리초 뒤에 만료되는 지
    private final long refreshExpirationMs;


    // [Application에 선언한 시크릿키 연결 및 배열 변환]
    public JwtTokenizer(
            @Value("${jwt.secretKey}") String accessSecret,
            @Value("${jwt.refreshKey}") String refreshSecret,
            @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {

        // yml에서 받은 문자열 -> byte로 배열 변환
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // [토큰 생성]
    // Access Tokens 생성
    public String createAccessToken(
            Long id, String email, String name, String username){

        // 실제 생성 로직(createToken)에 "Access용" 만료시간과 "Access용" 비밀키를 넘겨서 위임
        return createToken(id, email, name, username, accessExpirationMs, accessSecret);

    }

    // Refresh Token 생성
    public String createRefreshToken(
            Long id, String email, String name, String username){

        // 실제 생성 로직(createToken)에 "Access용" 만료시간과 "Access용" 비밀키를 넘겨서 위임
        return createToken(id, email, name, username, refreshExpirationMs, refreshSecret);
    }

    // 진짜 토큰 생성
    private String createToken(
            Long id, String email, String name, String username, Long expire, byte[] secretKey){

        // 토큰 생성 후  유효시간
        Date now = new Date(); // 현재 시간
        Date expiration = new Date(now.getTime() + expire); // 현재 시간 + 유효 시간 = 만료시간

        // JWT builder 기본 API
        return Jwts.builder()
                .subject(username)                // 토큰 주인의 아이디
                .claim("email",email)        // payload에 email 담기
                .claim("name",name)          // payload에 이름 담기
                .claim("userId", id)         // payload에 User의 DB id 담기
                .issuedAt(now)                    // 발급 시각 기록
                .expiration(expiration)           // 만료 시각 기록
                .signWith(getSigingKey(secretKey))  // secretKey로 서명(도장 찍기)
                .compact();                  // "header.payload.signature" 문자열로 완성
    }

    // byte[] 형태의 비밀키를 JWT 라이브러리가 요구하는 SecretKey 객체로 변환
    private SecretKey getSigingKey(byte[] secretKey){
        return Keys.hmacShaKeyFor(secretKey);
    }

    // 토큰 파싱(검증 + 해석)
    // "Bearer " 부분만 잘라내는 헬퍼
    private String removeBearerPrefix(String token){
        if(token == null){
            return null;
        }
        if(token.startsWith("Bearer ")){
            return token.substring(7); // "Bearer 앞에 7자리 삭제"
        }
        return token;
    }

    //토큰 검증 claims 꺼내기
    private Claims parseToken(String token, byte[] secret){
        token = removeBearerPrefix(token); // "Bearer "가 붙어왔다면 제거

        // JJWT라이브러리 토큰 검증
        return Jwts.parser()
                .verifyWith(getSigingKey(secret)) // 발급 서명 같은 키로 검증
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Access Token 전용 파싱 메서드
// - accessToken 문자열을 받아서
// - 위에서 만든 parseToken()에게 "accessSecret 키로 검증해줘"라고 위임
// - 로그인 이후 매 API 요청마다, JwtAuthenticationFilter가 이 메서드를 호출해서 토큰을 검증함
    public Claims parseAccessToken(String accessToken){
        return parseToken(accessToken,accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken){
        return parseToken(refreshToken,refreshSecret);
    }

    // Refresh Token 전용 파싱 메서드
// - refreshToken 문자열을 받아서
// - parseToken()에게 "refreshSecret 키로 검증해줘"라고 위임 (accessSecret이 아닌 다른 키 사용!)
// - Access Token이 만료됐을 때, 재발급 API에서 이 메서드로 refreshToken을 검증함
    public Long getUserIdFromToken(String token){
        token = removeBearerPrefix(token);                  // Bearer 제거
        Claims claims = parseToken(token, accessSecret);
        return claims.get("userId",Long.class);             // userId만 Long 타입으로 리턴
    }

    // Access Token 만료시간(ms) getter
    public Long getAccessTokenExpireCount(){
        return accessExpirationMs;
    }

    // Refresh Token 만료시간(ms) getter
    public Long getRefreshTokenExpireCount(){
        return refreshExpirationMs;
    }

}