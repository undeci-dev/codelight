package com.project.codelight.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenUtils tokenUtils;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
        throws IOException, ServletException {

        List<String> notUseJwtUrlList = Arrays.asList(
            "/api/local-auth/login",
            "/api/local-auth/register"
        );

        if (notUseJwtUrlList.contains(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        try {
            if (StringUtils.isNotBlank(header)) {
                String token = TokenUtils.getHeaderToToken(header);
                if (tokenUtils.isValidToken(token)) {
                    String userId = tokenUtils.getClaimsToUserId(token);
                    if (StringUtils.isNotBlank(userId)) {
                        chain.doFilter(request, response);
                    } else {
                        throw new CodeLightException(
                            ExceptionCodeType.USER_NOT_FOUND);
                    }
                } else {
                    throw new CodeLightException(
                        ExceptionCodeType.TOKEN_INVALID);
                }
            } else {
                throw new CodeLightException(
                    ExceptionCodeType.TOKEN_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new CodeLightException(
                ExceptionCodeType.TOKEN_NOT_FOUND);
        }
    }
}