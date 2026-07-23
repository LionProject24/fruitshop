package com.lionproject24.fruitshop.security;

import com.lionproject24.fruitshop.entity.RoleType;
import com.lionproject24.fruitshop.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Spring Security 인증 객체로 사용할 커스텀 UserDetails 구현체
public class CustomUserDetails implements UserDetails {

    // User 엔티티를 통째로 들고 있음 - 필드 하나하나 복사하지 않고 원본 그대로 참조
    // 이렇게 하면 User에 필드가 추가돼도 이 클래스는 손댈 필요가 없고,
    // Controller/Service에서 User 전체 정보(id 포함)가 바로 필요할 때 재조회 없이 꺼내 쓸 수 있음
    private final User user;

    // Spring Security가 권한 체크할 때 쓰는 타입
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleType().name()));
    }

    public User getUser() {
        return user;
    }

    // Spring Security가 "권한 뭐야?" 할 때 물어보는 호출
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 비밀번호 검증할 때 Spring Security가 이 값을 씀 (암호화된 값)
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 로그인 아이디 검증할 때 사용
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 아래 4개는 계정 상태 체크용 (계정 잠김/만료 등) - 지금은 전부 true로 고정(기능 미사용)
    @Override // 계정 자체가 만료됐는지 (예: 1년짜리 임시 계정)
    public boolean isAccountNonExpired() {
        return true; // "만료 안 됐다" = 계정 유효
    }

    @Override
    public boolean isAccountNonLocked() { // 계정이 잠겼는지 (예: 로그인 5회 실패로 잠김)
        return true; // "안 잠겼다" = 로그인 가능
    }

    @Override
    public boolean isCredentialsNonExpired() { // 비밀번호가 만료됐는지 (예: 3개월마다 변경 강제 정책)
        return true; // "만료 안 됐다" = 현재 비밀번호 유효
    }

    @Override
    public boolean isEnabled() { // 계정이 활성화 상태인지 (예: 이메일 인증 대기중, 관리자가 비활성화)
        return true; // "활성화됨" = 사용 가능
    }
}