package com.project.codelight.auth.security.filter;

import com.project.codelight.auth.repository.TokenBlackListRepository;
import com.project.codelight.auth.service.dto.TokenValidationResult;
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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenBlackListRepository tokenBlackListRepository;

    private static final String ACCESS_TOKEN_HEADER_KEY = HttpHeaders.AUTHORIZATION;
    private static final List<String> notUseJwtUrlList = Arrays.asList(
        "/api/local-auth/login",
        "/api/local-auth/register",
        "/api/local-auth/token"
    );
    private static final String TOKEN_BLACK_LIST = "tokenBlackList";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
        throws IOException, ServletException {

        if (notUseJwtUrlList.contains(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String accessTokenHeader = request.getHeader(ACCESS_TOKEN_HEADER_KEY);

            if (StringUtils.isNotBlank(accessTokenHeader)) {
                String paramAccessToken = TokenUtils.getHeaderToToken(accessTokenHeader);

                TokenValidationResult tokenValidationResult = TokenUtils.isValidToken(
                    paramAccessToken);
                if (tokenValidationResult.isValid()) {
                    if (StringUtils.isNotBlank(TokenUtils.getClaimsToUserId(paramAccessToken))) {

                        if (tokenBlackListRepository.existsById(paramAccessToken)) {
                            throw new CodeLightException(ExceptionCodeType.TOKEN_BLACKLISTED);
                        }

                        chain.doFilter(request, response);
                    } else {
                        throw new CodeLightException(
                            ExceptionCodeType.TOKEN_INVALID);
                    }
                } else {
                    if (tokenValidationResult.getExceptionCodeTypeName().equals(
                        ExceptionCodeType.TOKEN_EXPIRED.name())) {
                        throw new CodeLightException(
                            ExceptionCodeType.TOKEN_EXPIRED);
                    }
                    throw new CodeLightException(
                        ExceptionCodeType.TOKEN_INVALID);
                }
            } else {
                throw new CodeLightException(
                    ExceptionCodeType.TOKEN_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new CodeLightException(
                ExceptionCodeType.TOKEN_REFRESH_FAILED);

        }
    }
}