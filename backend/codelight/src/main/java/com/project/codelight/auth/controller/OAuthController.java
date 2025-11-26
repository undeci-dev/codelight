package com.project.codelight.auth.controller;

import com.project.codelight.auth.dto.response.KakaoLoginResponse;
import com.project.codelight.auth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService OAuthService;

    @PostMapping("/api/oauth/kakao/login")
    public ResponseEntity<KakaoLoginResponse> login(@RequestParam String code) {
        KakaoLoginResponse response = OAuthService.login(code);
        return ResponseEntity.ok(response);
    }
}
