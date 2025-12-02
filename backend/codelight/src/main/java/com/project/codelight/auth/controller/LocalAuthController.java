package com.project.codelight.auth.controller;

import com.project.codelight.auth.dto.request.SignUpRequest;
import com.project.codelight.auth.dto.response.ReissueTokenResponse;
import com.project.codelight.auth.service.LocalAuthService;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LocalAuthController {

    private final LocalAuthService localAuthService;

    @PostMapping("/api/local-auth/register")
    public ResponseEntity<Void> register(@Valid @RequestBody SignUpRequest request) {
        Long userId = localAuthService.register(request);
        return ResponseEntity.created(URI.create("/api/local-auth/register/" + userId)).build();
    }

    @PostMapping("/api/local-auth/token")
    public ResponseEntity<Void> reissueToken(HttpServletRequest request,
                                             HttpServletResponse response) {
        String beforeRefreshToken = extractRefreshTokenFromCookie(request);
        ReissueTokenResponse newReissueTokenResponse = localAuthService.reissueToken(
            beforeRefreshToken);
        String newAccessToken = newReissueTokenResponse.getAccessToken();
        String newRefreshToken = newReissueTokenResponse.getRefreshToken();

        ResponseCookie refreshCookie = TokenUtils.createRefreshTokenCookie(newRefreshToken);
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok()
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
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        String headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (headerToken != null) {
            String accessToken = TokenUtils.getHeaderToToken(headerToken);
            if (accessToken != null) {
                localAuthService.addTokenBlackList(accessToken);
                localAuthService.removeRefreshToken(accessToken);
            } else {
                throw new CodeLightException(ExceptionCodeType.TOKEN_NOT_FOUND);
            }
        } else {
            throw new CodeLightException(ExceptionCodeType.TOKEN_NOT_FOUND);
        }

        ResponseCookie refreshCookie = TokenUtils.clearRefreshTokenCookie();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok().build();
    }
}