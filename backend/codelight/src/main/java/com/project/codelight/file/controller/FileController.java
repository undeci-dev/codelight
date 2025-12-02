package com.project.codelight.file.controller;

import com.project.codelight.file.dto.PresignedUrlRequest;
import com.project.codelight.file.dto.PresignedUrlResponse;
import com.project.codelight.file.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FileController {

    private final S3Service s3Service;

    @PostMapping("/api/files/presigned-url")
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(
        @Valid @RequestBody PresignedUrlRequest request) {
        PresignedUrlResponse response = s3Service.generatePresignedUrl(
            request.fileName(),
            request.contentType()
        );
        return ResponseEntity.ok(response);
    }
}
