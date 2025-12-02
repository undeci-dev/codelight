package com.project.codelight.file.service;

import com.project.codelight.file.dto.PresignedUrlResponse;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(10);

    public PresignedUrlResponse generatePresignedUrl(String fileName, String contentType) {
        String key = generateUniqueKey(fileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                            .bucket(bucket)
                                                            .key(key)
                                                            .contentType(contentType)
                                                            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                                                                        .signatureDuration(PRESIGNED_URL_DURATION)
                                                                        .putObjectRequest(putObjectRequest)
                                                                        .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();
        String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);

        return new PresignedUrlResponse(presignedUrl, s3Url);
    }

    private String generateUniqueKey(String fileName) {
        String extension = extractExtension(fileName);
        return "posts/" + UUID.randomUUID() + extension;
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
