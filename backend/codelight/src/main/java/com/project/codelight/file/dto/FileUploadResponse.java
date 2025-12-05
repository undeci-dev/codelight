package com.project.codelight.file.dto;

public record FileUploadResponse(
    String fileUrl,
    String s3Key,
    String fileName,
    Long fileSize,
    String contentType
) {
}
