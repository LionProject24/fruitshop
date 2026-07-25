package com.lionproject24.fruitshop.repository;

import com.lionproject24.fruitshop.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    //특정 id로 전부 지울 쿼리문
    void deleteAllByUserId(Long userId);
}
