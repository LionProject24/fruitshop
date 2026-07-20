package com.lionproject24.fruitshop.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

// Spring Security의 Authentication 인터페이스를 직접 구현한 커스텀 클래스
// JWT로 인증되었다는 정보를 SecurityContextHolder에 저장할 때 사용
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal; // 사용자 정보 (CustomUserDetails가 들어감)
    private Object credentials; // 인증 자격정보 (보통은 비밀번호, jwt기반으로 인증할 때는 null)

    // 인증 완료된 후에만 생성되는 생성자
    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials){
        super(authorities); // 부모 클래스에 권한 목록 전달
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true); // 인증 완료됨 표시
    }

    // 인증되기 전 사용되는 생성자 (토큰 문자열만 갖고 있는 상태)
    public JwtAuthenticationToken(String token){
        super(Collections.emptyList()); // 아직 권한 정보 없음
        this.principal = null; // 아직 사용자 정보 없음
        this.credentials=token; // 원본 토큰 문자열만 저장
        setAuthenticated(false); // 아직 인증 안 됨 표시
    }

    // Authentication 인터페이스가 요구하는 필수 메서드 - 자격정보 반환
    @Override
    public Object getCredentials(){
        return this.credentials;
    }

    // Authentication 인터페이스가 요구하는 필수 메서드 - 인증 주체(사용자 정보) 반환
    @Override
    public Object getPrincipal(){
        return this.principal;
    }

}
