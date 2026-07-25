package com.lionproject24.fruitshop.filter;

import com.lionproject24.fruitshop.exception.ErrorCode;
import com.lionproject24.fruitshop.security.CustomUserDetailsService;
import com.lionproject24.fruitshop.token.JwtAuthenticationToken;
import com.lionproject24.fruitshop.util.JwtTokenizer;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        // 요청해서 토큰 가져오고 검증 시도 시 예외처리
        String token = getToken(request);


        if (StringUtils.hasText(token)) {    // 토큰이 실제로 존재할 때만 검증 시도
            try {
                getAuthentication(token);   // 토큰 검증 + 인증 정보 등록 시도

                // 토큰의 유효기간이 지났을 때 발생하는 예외
            } catch (ExpiredJwtException e) {
                request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.name());
                log.error("Expried Token : {} ", token, e);
                throw new BadCredentialsException("Expired token exception", e);

                // 우리 시스템이 지원하지 않는 형식의 토큰일 때 발생
            } catch (UnsupportedJwtException e) {
                request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN.name());
                log.error("Unsupported Token: {}", token, e);
                throw new BadCredentialsException("Unsupported token exception", e);

                // 토큰 문자열 자체가 형식에 안 맞게 깨져있을 때 발생 (위조 의심)
            } catch (MalformedJwtException e) {
                request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
                log.error("Invalid Token: {}", token, e);
                throw new BadCredentialsException("Invalid token exception", e);

                // 토큰이 아예 없거나 빈 값일 때 생성
            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", ErrorCode.NOT_FOUND_TOKEN.name());
                throw new BadCredentialsException("Token not found exception", e);

                // 위 4개에 해당 안 되는, 예상 못한 나머지 모든 오류
            } catch (Exception e) {
                log.error("JWT Filter - Internal Error : {}", token, e);
                throw new BadCredentialsException("JWT Filter internal exception ", e);
            }

        }

        // 다음 필터 or 컨트롤러로 요청 넘기기
        filterChain.doFilter(request, response);
    }

    // 토큰을 검증하고 성공하면 SecurityContextHolder에 " 이사람은 인즌됐다"고 등록하는 메서드
    private void getAuthentication(String token) {

        // 1. 토큰 서명을 검증하고, 통과하면 payload(Claims)를 꺼내옴
        // 여기서 서명이 안맞거나 만료되면 예외가 터지고, doFilterInternal의 catch가 잡음
        Claims claims = jwtTokenizer.parseAccessToken(token);

        // 2. payload 안에 담겨있던 값들을 하나씩 꺼냄
        String username = claims.getSubject();                   // 로그인 ID
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // 3. "인증 완료" 상태의 Authentication 객체 생성
        Authentication authentication = new JwtAuthenticationToken(userDetails.getAuthorities(), userDetails, null);

        // 4. 이 요청의 보안 컨텍스트에 인증 정보를 등록(최종 목록)
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 요청(request)에서 토큰 문자열을 꺼내오는 메서드
    private String getToken(HttpServletRequest request) {

        // 1순위: Authorization 헤더 확인
        String authorization = request.getHeader("Authorization");
        // 헤더 값이 있고, "Bearer "로 시작한다면
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            // "Bearer " 7글자를 잘라내고 순수 토큰 문자열만 반환
            return authorization.substring(7);
        }

        // 2순위: 헤더에 없다면 쿠키 목록을 확인
        Cookie[] cookies = request.getCookies();
        // 쿠키가 하나라도 있다면
        if (cookies != null) {
            // 쿠키들을 하나씩 순회하면서
            for (Cookie cookie : cookies) {
                // 이름이 "accessToken"인 쿠키를 찾으면
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue(); // 그 값을 반환
                }
            }
        }

        // 헤더에도, 쿠키에도 토큰이 없다면 null 반환
        return null;
    }
}