package org.example.frusitshopapp.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.frusitshopapp.entity.User;
import org.example.frusitshopapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 인증 시도:: {}",username);

        User user = userRepository.findByUsername(username).orElseThrow(()->
                new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return new CustomUserDetails(user);
    }
}
