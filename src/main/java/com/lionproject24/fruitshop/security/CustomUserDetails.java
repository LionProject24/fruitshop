package com.lionproject24.fruitshop.security;


import com.lionproject24.fruitshop.entity.RoleType;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

// Spring Security 인증 객체로 사용할 커스텀 USerDetails 구현체
public class CustomUserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final String name;
    private final String email;
    // Spring Security가 권한 체크할 때 쓰는 타입
    private final List<GrantedAuthority> authorities;

    // roles(문자열 리스트)를 받아서, authorities(GrantedAuthority 리스트)로 변환해 저장
    @Builder
    public CustomUserDetails(String username, String password, String name, String email, RoleType roleType){
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;

        // roleType에서 ROLE_USER 같은 형태로 감싸서 리스트에 담음
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
    }

    // UserDetails 인터페이스엔 없지만, 필요해서 추가한 커스텀 getter
    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    // Spring Security가 "권한 뭐야?"할 때 물어보는 호출
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    // 비밀번호 검증할 때 Spring Security가 이 값을 씀 (암호화된 값)
    @Override
    public String getPassword(){
        return password;
    }

    // 로그인 아이디 검증할 때 사용
    @Override
    public String getUsername(){
        return username;
    }

    // 아래 4개는 계정 상태 체크용 (계정 잠김/만료 등) - 지금은 전부 true로 고정(기능 미사용)
    @Override // 계정 자체가 만료됐는지 (예: 1년짜리 임시 계정)
    public boolean isAccountNonExpired(){
        return true; // "만료 안 됐다" = 계정 유효
    }

    @Override
    public boolean isAccountNonLocked(){ //계정이 잠겼는지 (예: 로그인 5회 실패로 잠김)
        return true; // "안 잠겼다" = 로그인 가능
    }

    @Override
    public boolean isCredentialsNonExpired(){ // 비밀번호가 만료됐는지 (예: 3개월마다 변경 강제 정책)
        return true; // "만료 안 됐다" = 현재 비밀번호 유효
    }
    @Override
    public boolean isEnabled() { //계정이 활성화 상태인지 (예: 이메일 인증 대기중, 관리자가 비활성화)
        return true; //"활성화됨" = 사용 가능
    }
}