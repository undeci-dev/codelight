package com.project.codelight.auth.controller;

import com.project.codelight.auth.service.AuthService;
import com.project.codelight.user.dto.SignUpRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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
}
