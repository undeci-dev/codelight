package com.project.codelight.file.dto;

public record PresignedUrlResponse(
    String presignedUrl,
    String s3Url
) {
}
