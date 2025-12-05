package com.project.codelight.post.dto.request;

public record FileOrderUpdate(
    Long fileId,
    int displayOrder
) {
}
