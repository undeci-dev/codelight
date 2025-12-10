package com.project.codelight.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.auth.repository.TokenBlackListRepository;
import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.auth.service.model.TokenValidationResult;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.global.exception.dto.ExceptionResponse;
import com.project.codelight.user.domain.User;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenBlackListRepository tokenBlackListRepository;
    private final ObjectMapper objectMapper;

    private static final Set<String> SKIP_URLS = Set.of(
        "/api/local-auth/login",
        "/api/local-auth/register",
        "/api/auth/token"
    );

    private static final Set<String> SKIP_URL_PREFIXES = Set.of(
        "/oauth2/",
        "/login/oauth2/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
        throws IOException, ServletException {

        if (shouldSkipFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        String accessToken = extractToken(request);

        // 토큰이 없으면 인증 없이 다음 필터로 진행 (Security의 authorizeHttpRequests에서 처리)
        if (accessToken == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            authenticateWithToken(accessToken);
            chain.doFilter(request, response);
        } catch (CodeLightException e) {
            sendErrorResponse(request, response, e.getExceptionCodeType());
        }
    }

    private boolean shouldSkipFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return SKIP_URLS.contains(uri) ||
            SKIP_URL_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(header)) {
            return null;
        }
        return TokenUtils.getHeaderToToken(header);
    }

    private void authenticateWithToken(String token) {
        TokenValidationResult validationResult = TokenUtils.isValidToken(token);

        if (!validationResult.isValid()) {
            throw createTokenException(validationResult);
        }

        String userId = TokenUtils.getClaimsToUserId(token);
        if (StringUtils.isBlank(userId)) {
            throw new CodeLightException(ExceptionCodeType.TOKEN_INVALID);
        }

        if (tokenBlackListRepository.existsById(token)) {
            throw new CodeLightException(ExceptionCodeType.TOKEN_BLACKLISTED);
        }

        setAuthentication(token);
    }

    private CodeLightException createTokenException(TokenValidationResult validationResult) {
        if (ExceptionCodeType.TOKEN_EXPIRED.getExceptionCode().name()
                                           .equals(validationResult.getExceptionCodeTypeName())) {
            return new CodeLightException(ExceptionCodeType.TOKEN_EXPIRED);
        }
        return new CodeLightException(ExceptionCodeType.TOKEN_INVALID);
    }

    private void setAuthentication(String token) {
        User user = TokenUtils.getClaimsToUserDto(token);
        CustomUserDetails userDetails = new CustomUserDetails(user, Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendErrorResponse(HttpServletRequest request,
                                   HttpServletResponse response,
                                   ExceptionCodeType exceptionCodeType) throws IOException {
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
