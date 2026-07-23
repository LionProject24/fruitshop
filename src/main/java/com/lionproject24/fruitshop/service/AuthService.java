package com.lionproject24.fruitshop.service;

import com.lionproject24.fruitshop.dto.LoginRequestDto;
import com.lionproject24.fruitshop.dto.LoginResponseDto;
import com.lionproject24.fruitshop.dto.SignupRequestDto;
import com.lionproject24.fruitshop.entity.RefreshToken;
import com.lionproject24.fruitshop.entity.RoleType;
import com.lionproject24.fruitshop.entity.User;
import com.lionproject24.fruitshop.exception.ErrorCode;
import com.lionproject24.fruitshop.repository.UserRepository;
import com.lionproject24.fruitshop.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.lionproject24.fruitshop.exception.CustomException;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;


    // 1. 회원가입
    @Transactional
    public void signup(SignupRequestDto requestDto) {

    // 2. 아이디 중복 확인
    if (userRepository.findByUsername(requestDto.getUsername()).isPresent()){
        throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }

    // 3. 비밀번호 암호화, 저장.
    User user = User.builder()
        .username(requestDto.getUsername())
        .name(requestDto.getName())
        .email(requestDto.getEmail())
        .password(passwordEncoder.encode(requestDto.getPassword()))
                .roleType(RoleType.USER)
                        .build();
    userRepository.save(user);
    }

    //    로그인
    //    1. 유저찾기
    public LoginResponseDto login(LoginRequestDto requestDto){
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(()->
                new CustomException(ErrorCode.USER_NOT_FOUND));

//        2. 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

//        3.토큰 발급
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(), user.getEmail(), user.getName(), user.getUsername());
        String refreshToken = jwtTokenizer.createRefreshToken(
                user.getId(), user.getEmail(), user.getName(), user.getUsername());

        // 4-0. 기존에 저장된 refreshToken이 있으면 먼저 삭제 (중복 로그인 방지)
        refreshTokenService.deleteAllByUserId(user.getId());

        // 4. refreshToken을 DB에 저장 (재발급 검증용)
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .build();
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    // Access Token 재발급
    @Transactional
    public LoginResponseDto reissueAccessToken(String refreshToken){
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
        }

        // 1. refreshToken 자체가 유효한 토큰인지 검증
        Claims claims;
        try{
            claims = jwtTokenizer.parseRefreshToken(refreshToken);
        } catch (ExpiredJwtException e){
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 2. DB에 저장된 refreshToken과 일치하는 지 확인
        RefreshToken dbToken = refreshTokenService.findRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
        if (!refreshToken.equals(dbToken.getToken())){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 3. 토큰 속 userId로 유저 다시 조회
        Long userId = claims.get("userId", Long.class);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //4. 새 accessToken만 발급 (refreshToken은 재사용)
        String newAccessToken = jwtTokenizer.createAccessToken(
                user.getId(), user.getEmail(), user.getName(), user.getUsername());

        return LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
