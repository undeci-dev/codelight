package com.project.codelight.auth.security.handler;

import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        try {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

            String userEmail = token.getName();
            String userPw = (String) token.getCredentials();

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(
                userEmail);

            if (!passwordEncoder.matches(userPw, userDetails.getPassword())) {
                throw new CodeLightException(ExceptionCodeType.USER_INVALID_PASSWORD);
            }
            return new UsernamePasswordAuthenticationToken(userDetails, userPw,
                userDetails.getAuthorities());
        } catch (AuthenticationException e) {
            throw new CodeLightException(ExceptionCodeType.USER_LOGIN_REQUEST_ERROR);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
