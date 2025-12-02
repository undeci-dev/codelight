package com.project.codelight.post.dto.request;

import com.project.codelight.post.dto.request.PostCreateRequest.FileInfo;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PostUpdateRequest(
    @NotBlank(message = "내용은 필수입니다.")
    String content,

    List<FileInfo> files
) {
}
