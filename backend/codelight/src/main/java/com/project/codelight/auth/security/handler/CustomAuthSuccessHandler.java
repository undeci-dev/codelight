package com.project.codelight.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.auth.constants.TokenExpiration;
import com.project.codelight.auth.domain.RefreshToken;
import com.project.codelight.auth.repository.RefreshTokenRepository;
import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

        Map<String, Object> responseMap = new HashMap<>();

        if (user.isDeleted()) {
            responseMap.put("status", HttpStatus.FORBIDDEN.value());
            responseMap.put("codeLightCode", ExceptionCodeType.USER_ACCOUNT_DELETED);
        } else {
            String accessToken = TokenUtils.generateAccessToken(user);
            String refreshToken = TokenUtils.generateRefreshToken(user);

            response.setHeader("Authorization", "Bearer " + accessToken);

            responseMap.put("status", HttpStatus.OK.value());
            responseMap.put("codeLightCode", null);

            ResponseCookie refreshCookie = TokenUtils.createRefreshTokenCookie(refreshToken);
            response.addHeader("Set-Cookie", refreshCookie.toString());

            // Redis에 RefreshToken 저장
            RefreshToken redisRefreshToken = RefreshToken.builder()
                                                         .userId(user.getId())
                                                         .token(refreshToken)
                                                         .expiration(TokenExpiration.REFRESH_TOKEN.getExpirationInSeconds())
                                                         .build();
            refreshTokenRepository.save(redisRefreshToken);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();
        printWriter.write(objectMapper.writeValueAsString(responseMap));
        printWriter.flush();
        printWriter.close();
    }
}