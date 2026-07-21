package com.lionproject24.fruitshop.controller;

import com.lionproject24.fruitshop.dto.LoginRequestDto;
import com.lionproject24.fruitshop.dto.LoginResponseDto;
import com.lionproject24.fruitshop.dto.SignupRequestDto;
import com.lionproject24.fruitshop.exception.CustomException;
import com.lionproject24.fruitshop.exception.ErrorCode;
import com.lionproject24.fruitshop.service.AuthService;
import com.lionproject24.fruitshop.util.JwtTokenizer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    // 쿠키 만료 시간
    private final JwtTokenizer jwtTokenizer;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto){
        authService.signup(requestDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto,
                                                  HttpServletResponse response){

        // 1. 아이디/비번 검증
        LoginResponseDto responseDto = authService.login(requestDto);

        // 2. 발급받은 토큰 2개(access, refresh)를 각각 HttpOnly 쿠키로 응답
        addTokenCookie("accessToken", responseDto.getAccessToken(), jwtTokenizer.getAccessTokenExpireCount(), response);
        addTokenCookie("refreshToken", responseDto.getRefreshToken(), jwtTokenizer.getRefreshTokenExpireCount(), response);

        // 3. 응답 body에도 토큰/유저 정보를 같이 내려줌
        return ResponseEntity.ok(responseDto);
    }

    // Access Token 재발급 API
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request, HttpServletResponse response){

        // 1. 요청에 담긴 쿠키에서 refreshToken 꺼내기
        String refreshToken = getTokenFromCookie(request, "refreshToken");
        if (refreshToken == null){

            // 쿠키 자체가 없으면 재발급 시도하지 않고 바로 에러
            throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
        }

        // 2. refreshToken 검증 + 새 accessToken 발급처리
        LoginResponseDto responseDto = authService.reissueAccessToken(refreshToken);

        // 3. 새로 발급된 accessToken만 쿠키로 갱신 (refreshToken은 그대로 유지)
        addTokenCookie("accessToken", responseDto.getAccessToken(), jwtTokenizer.getAccessTokenExpireCount(), response);
        return ResponseEntity.ok(responseDto);
    }

    // 일치하는 쿠키 값 꺼내기
    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // 쿠키가 없을 경우 null
        return null;
    }
        // 발급받은 토큰을 브라우저 쿠키로 응답해주는 메서드
        private void addTokenCookie(String cookieName, String cookieValue, Long expireCount, HttpServletResponse response) {
            Cookie cookie = new Cookie(cookieName, cookieValue);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(Math.toIntExact(expireCount / 1000));
            response.addCookie(cookie);
        }
    }