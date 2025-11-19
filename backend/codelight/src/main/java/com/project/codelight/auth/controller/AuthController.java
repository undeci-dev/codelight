package com.project.codelight.auth.controller;

import com.project.codelight.auth.dto.request.SignUpRequest;
import com.project.codelight.auth.dto.response.ReissueTokenResponse;
import com.project.codelight.auth.service.AuthService;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/local-auth/register")
    public ResponseEntity<Void> register(@Valid @RequestBody SignUpRequest request) {
        Long userId = authService.register(request);
        return ResponseEntity.created(URI.create("/api/local-auth/register/" + userId)).build();
    }

    @PostMapping("/api/local-auth/token")
    public ResponseEntity<Void> reissueToken(HttpServletRequest request) {
        String beforeRefreshToken = extractRefreshTokenFromCookie(request);
        ReissueTokenResponse newReissueTokenResponse = authService.reissueToken(beforeRefreshToken);
        String newAccessToken = newReissueTokenResponse.getAccessToken();
        String newRefreshToken = newReissueTokenResponse.getRefreshToken();

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE,
                                 TokenUtils.createRefreshTokenCookie(newRefreshToken).toString())
                             .header(HttpHeaders.AUTHORIZATION, newAccessToken)
                             .build();
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

    @PostMapping("/api/local-auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (headerToken != null) {
            String accessToken = TokenUtils.getHeaderToToken(headerToken);
            if (accessToken != null) {
                authService.addTokenBlackList(accessToken);
                authService.removeRefreshToken(accessToken);
            } else {
                throw new CodeLightException(ExceptionCodeType.TOKEN_NOT_FOUND);
            }
        } else {
            throw new CodeLightException(ExceptionCodeType.TOKEN_NOT_FOUND);
        }
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
            TokenUtils.clearRefreshTokenCookie().toString()).build();
    }
}