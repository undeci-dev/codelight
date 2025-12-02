package com.project.codelight.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PostCreateRequest(
    @NotBlank(message = "내용은 필수입니다.")
    String content,

    List<FileInfo> files
) {
    public record FileInfo(
        String fileUrl,
        String fileName,
        Long fileSize
    ) {
    }
}
