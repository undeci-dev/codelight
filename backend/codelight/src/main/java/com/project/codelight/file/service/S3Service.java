package com.project.codelight.file.service;

import com.project.codelight.file.dto.FileUploadResponse;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public FileUploadResponse uploadFile(MultipartFile file) {
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        String s3Key = generateUniqueKey(originalFileName);
        String contentType = file.getContentType();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .bucket(bucket)
                                                                .key(s3Key)
                                                                .contentType(contentType)
                                                                .build();

            s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region,
                s3Key);

            return new FileUploadResponse(
                fileUrl,
                s3Key,
                originalFileName,
                file.getSize(),
                contentType
            );
        } catch (IOException e) {
            throw new CodeLightException(ExceptionCodeType.FILE_UPLOAD_FAILED);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CodeLightException(ExceptionCodeType.IMAGE_FILE_REQUIRED);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CodeLightException(ExceptionCodeType.IMAGE_SIZE_EXCEEDED);
        }
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

    public void deleteFile(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            return;
        }

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                                                                   .bucket(bucket)
                                                                   .key(s3Key)
                                                                   .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", s3Key, e);
        }
    }

    public void deleteFiles(List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return;
        }

        List<String> validKeys = s3Keys.stream()
                                       .filter(key -> key != null && !key.isBlank())
                                       .toList();

        if (validKeys.isEmpty()) {
            return;
        }

        try {
            List<ObjectIdentifier> objects = validKeys.stream()
                                                      .map(
                                                          key -> ObjectIdentifier.builder().key(key)
                                                                                 .build())
                                                      .toList();

            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                                                                     .bucket(bucket)
                                                                     .delete(Delete.builder()
                                                                                   .objects(objects)
                                                                                   .build())
                                                                     .build();

            s3Client.deleteObjects(deleteRequest);
        } catch (Exception e) {
            log.error("S3 파일 일괄 삭제 실패", e);
        }
    }
}
