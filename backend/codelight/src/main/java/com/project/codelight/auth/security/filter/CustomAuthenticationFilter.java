package com.project.codelight.auth.security.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                      ObjectMapper objectMapper) {
        super.setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
        throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest;
        try {
            authRequest = getAuthRequest(request);
            setDetails(request, authRequest);
        } catch (Exception e) {
            throw new CodeLightException(ExceptionCodeType.USER_LOGIN_REQUEST_ERROR);
        }
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) {
        try {
            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        } catch (UsernameNotFoundException e) {
            throw new CodeLightException(ExceptionCodeType.USER_NOT_FOUND);
        } catch (IOException e) {
            throw new CodeLightException(ExceptionCodeType.USER_LOGIN_REQUEST_ERROR);
        }
    }

}