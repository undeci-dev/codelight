package com.project.codelight.auth.controller;

import com.project.codelight.auth.dto.request.SignUpRequest;
import com.project.codelight.auth.service.LocalAuthService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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
}