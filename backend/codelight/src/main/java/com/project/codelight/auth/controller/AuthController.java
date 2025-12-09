package com.project.codelight.auth.controller;

import com.project.codelight.auth.dto.response.ReissueTokenResponse;
import com.project.codelight.auth.service.AuthService;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/auth/token")
    public ResponseEntity<Void> reissueToken(HttpServletRequest request,
                                             HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        ReissueTokenResponse tokenResponse = authService.reissueToken(refreshToken);

        ResponseCookie refreshCookie = TokenUtils.createRefreshTokenCookie(
            tokenResponse.getRefreshToken());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok()
                             .header(HttpHeaders.AUTHORIZATION, tokenResponse.getAccessToken())
                             .build();
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        String headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (headerToken == null) {
            throw new CodeLightException(ExceptionCodeType.TOKEN_NOT_FOUND);
        }

        String accessToken = TokenUtils.getHeaderToToken(headerToken);
        if (accessToken == null) {
            throw new CodeLightException(ExceptionCodeType.TOKEN_NOT_FOUND);
        }

        authService.logout(accessToken);

        ResponseCookie refreshCookie = TokenUtils.clearRefreshTokenCookie();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CodeLightException(ExceptionCodeType.TOKEN_NOT_FOUND);
        }

        return Arrays.stream(cookies)
                     .filter(cookie -> "refreshToken".equals(cookie.getName()))
                     .findFirst()
                     .map(Cookie::getValue)
                     .orElseThrow(() -> new CodeLightException(ExceptionCodeType.TOKEN_NOT_FOUND));
    }
}
