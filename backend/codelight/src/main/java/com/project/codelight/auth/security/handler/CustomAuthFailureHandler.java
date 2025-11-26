package com.project.codelight.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.global.exception.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        ExceptionCodeType exceptionCodeType;

        if (exception.getCause() instanceof CodeLightException codeLightException) {
            exceptionCodeType = codeLightException.getExceptionCodeType();
        } else {
            exceptionCodeType = ExceptionCodeType.USER_INVALID_CREDENTIALS;
        }

        ExceptionResponse exceptionResponse = new ExceptionResponse(
            request.getMethod(),
            request.getRequestURI(),
            exceptionCodeType.getExceptionCode().name(),
            exceptionCodeType.getMessage()
        );

        response.setStatus(exceptionCodeType.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
    }
}