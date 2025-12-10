package com.project.codelight.auth.security.oauth2;

import com.project.codelight.global.exception.ExceptionCodeType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${cors.allowed-origins[0]}")
    private String frontendUrl;

    @Value("${oauth.callback-path}")
    private String callbackPath;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        ExceptionCodeType errorType = mapToExceptionCodeType(exception);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + callbackPath)
            .queryParam("error", errorType.getExceptionCode().name())
            .queryParam("message", errorType.getMessage())
            .build()
            .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private ExceptionCodeType mapToExceptionCodeType(AuthenticationException exception) {
        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            OAuth2Error error = oauth2Exception.getError();
            String errorCode = error.getErrorCode();

            // CodeLightException에서 변환된 경우 (ExceptionCode enum 이름과 매칭)
            ExceptionCodeType mappedType = findByExceptionCodeName(errorCode);
            if (mappedType != null) {
                return mappedType;
            }

            // Spring Security 기본 OAuth2 에러 코드 매핑
            return switch (errorCode) {
                case "invalid_token", "invalid_token_response" -> ExceptionCodeType.OAUTH_TOKEN_REQUEST_FAILED;
                case "invalid_request" -> ExceptionCodeType.OAUTH_CALLBACK_ERROR;
                case "access_denied", "authorization_request_not_found" -> ExceptionCodeType.OAUTH_PROVIDER_ERROR;
                case "server_error" -> ExceptionCodeType.OAUTH_SERVER_ERROR;
                case "invalid_user_info_response" -> ExceptionCodeType.OAUTH_USER_INFO_REQUEST_FAILED;
                case "invalid_id_token" -> ExceptionCodeType.OAUTH_TOKEN_REQUEST_FAILED;
                default -> ExceptionCodeType.OAUTH_PROVIDER_ERROR;
            };
        }
        return ExceptionCodeType.OAUTH_PROVIDER_ERROR;
    }

    private ExceptionCodeType findByExceptionCodeName(String errorCode) {
        return Arrays.stream(ExceptionCodeType.values())
            .filter(type -> type.getExceptionCode().name().equals(errorCode))
            .findFirst()
            .orElse(null);
    }
}
