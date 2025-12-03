package com.project.codelight.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 200, message = "댓글은 최대 200자까지 입력 가능합니다.")
    String content
) {
}
