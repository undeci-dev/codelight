package com.project.codelight.link.controller;

import com.project.codelight.link.dto.request.LinkPreviewRequest;
import com.project.codelight.link.dto.response.LinkPreviewResponse;
import com.project.codelight.link.service.LinkPreviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LinkController {

    private final LinkPreviewService linkPreviewService;

    @PostMapping("/api/link/preview")
    public ResponseEntity<LinkPreviewResponse> getLinkPreview(
        @Valid @RequestBody LinkPreviewRequest request) {

        LinkPreviewResponse response = linkPreviewService.fetchLinkPreview(request.url());
        return ResponseEntity.ok(response);
    }
}
