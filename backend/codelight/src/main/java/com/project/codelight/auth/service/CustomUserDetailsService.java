package com.project.codelight.auth.service;

import com.project.codelight.auth.repository.CustomUserDetailsRepository;
import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomUserDetailsRepository customUserDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) {
        return login(userEmail)
            .map(user -> new CustomUserDetails(user,
                Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().name()))))
            .orElseThrow(
                () -> new CodeLightException(ExceptionCodeType.USER_INVALID_CREDENTIALS));
    }

    private Optional<User> login(String userEmail) {
        return customUserDetailsRepository.findByEmailAndLoginType(userEmail, LoginType.LOCAL);
    }
}