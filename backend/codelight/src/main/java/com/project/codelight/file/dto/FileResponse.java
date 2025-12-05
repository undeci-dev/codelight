package com.project.codelight.file.dto;

import com.project.codelight.post.domain.PostFile;

public record FileResponse(
    Long fileId,
    String fileUrl,
    String s3Key,
    String fileName,
    Long fileSize,
    int displayOrder
) {
    public static FileResponse from(PostFile postFile, String cloudFrontDomain) {
        String fileUrl = "https://" + cloudFrontDomain + "/" + postFile.getS3Key();
        return new FileResponse(
            postFile.getId(),
            fileUrl,
            postFile.getS3Key(),
            postFile.getFileName(),
            postFile.getFileSize(),
            postFile.getDisplayOrder()
        );
    }
}
