package com.lionproject24.fruitshop.service;

import com.lionproject24.fruitshop.entity.RefreshToken;
import com.lionproject24.fruitshop.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    // 저장 - 로그인 성공 시 새 Refresh Token을 DB에 저장
    @Transactional
    public RefreshToken addRefreshToken(RefreshToken refreshToken){
        return refreshTokenRepository.save(refreshToken);
    }

    // 조회 - 재발급 요청 시, 클라이언트가 보낸 토근이 DB에 실제로 있는지 확인
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findRefreshToken(String refreshToken){
        return refreshTokenRepository.findByToken(refreshToken);
    }

    // 삭제 - 로그아웃 등에서 특정 토큰을 무효화할 때 사용
    @Transactional
    public void deleteRefreshToken(String refreshToken){
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete); // 있으면 삭제, 없으면 아무것도 안함
    }

    // 삭제 - 로그인 시 기존 토큰 정리 (중복 로그인 방지) 해당유저의 모든 토큰 싹 다 삭제
    @Transactional
    public void deleteAllByUserId(Long userId){
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
