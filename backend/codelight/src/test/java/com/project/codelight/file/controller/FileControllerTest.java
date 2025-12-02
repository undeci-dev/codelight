package com.project.codelight.file.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.file.dto.PresignedUrlRequest;
import com.project.codelight.file.dto.PresignedUrlResponse;
import com.project.codelight.file.service.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private S3Service s3Service;

    @Nested
    @DisplayName("Presigned URL 발급 API")
    class GetPresignedUrlTest {

        @Test
        @DisplayName("Presigned URL을 성공적으로 발급한다")
        @WithMockUser
        void getPresignedUrlSuccess() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest(
                "test-image.jpg",
                "image/jpeg"
            );

            PresignedUrlResponse response = new PresignedUrlResponse(
                "https://bucket.s3.amazonaws.com/posts/uuid-test-image.jpg?X-Amz-Algorithm=...",
                "https://bucket.s3.ap-northeast-2.amazonaws.com/posts/uuid-test-image.jpg"
            );

            given(s3Service.generatePresignedUrl(anyString(), anyString())).willReturn(response);

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.presignedUrl").exists())
                   .andExpect(jsonPath("$.s3Url").exists());
        }

        @Test
        @DisplayName("파일명이 비어있으면 400을 반환한다")
        @WithMockUser
        void getPresignedUrlWithEmptyFileName() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest("", "image/jpeg");

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("파일명이 null이면 400을 반환한다")
        @WithMockUser
        void getPresignedUrlWithNullFileName() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest(null, "image/jpeg");

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("컨텐츠 타입이 비어있으면 400을 반환한다")
        @WithMockUser
        void getPresignedUrlWithEmptyContentType() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest("test.jpg", "");

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("컨텐츠 타입이 null이면 400을 반환한다")
        @WithMockUser
        void getPresignedUrlWithNullContentType() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest("test.jpg", null);

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("다양한 이미지 타입의 파일에 대해 Presigned URL을 발급한다")
        @WithMockUser
        void getPresignedUrlForVariousImageTypes() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest(
                "photo.png",
                "image/png"
            );

            PresignedUrlResponse response = new PresignedUrlResponse(
                "https://bucket.s3.amazonaws.com/posts/uuid-photo.png?X-Amz-Algorithm=...",
                "https://bucket.s3.ap-northeast-2.amazonaws.com/posts/uuid-photo.png"
            );

            given(s3Service.generatePresignedUrl("photo.png", "image/png")).willReturn(response);

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.presignedUrl").value(response.presignedUrl()))
                   .andExpect(jsonPath("$.s3Url").value(response.s3Url()));
        }

        @Test
        @DisplayName("비디오 파일에 대해 Presigned URL을 발급한다")
        @WithMockUser
        void getPresignedUrlForVideoFile() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest(
                "video.mp4",
                "video/mp4"
            );

            PresignedUrlResponse response = new PresignedUrlResponse(
                "https://bucket.s3.amazonaws.com/posts/uuid-video.mp4?X-Amz-Algorithm=...",
                "https://bucket.s3.ap-northeast-2.amazonaws.com/posts/uuid-video.mp4"
            );

            given(s3Service.generatePresignedUrl("video.mp4", "video/mp4")).willReturn(response);

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.presignedUrl").exists())
                   .andExpect(jsonPath("$.s3Url").exists());
        }

        @Test
        @DisplayName("확장자가 없는 파일에 대해서도 Presigned URL을 발급한다")
        @WithMockUser
        void getPresignedUrlForFileWithoutExtension() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest(
                "README",
                "text/plain"
            );

            PresignedUrlResponse response = new PresignedUrlResponse(
                "https://bucket.s3.amazonaws.com/posts/uuid?X-Amz-Algorithm=...",
                "https://bucket.s3.ap-northeast-2.amazonaws.com/posts/uuid"
            );

            given(s3Service.generatePresignedUrl("README", "text/plain")).willReturn(response);

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.presignedUrl").exists())
                   .andExpect(jsonPath("$.s3Url").exists());
        }

        @Test
        @DisplayName("한글 파일명에 대해서도 Presigned URL을 발급한다")
        @WithMockUser
        void getPresignedUrlForKoreanFileName() throws Exception {
            PresignedUrlRequest request = new PresignedUrlRequest(
                "한글파일명.jpg",
                "image/jpeg"
            );

            PresignedUrlResponse response = new PresignedUrlResponse(
                "https://bucket.s3.amazonaws.com/posts/uuid.jpg?X-Amz-Algorithm=...",
                "https://bucket.s3.ap-northeast-2.amazonaws.com/posts/uuid.jpg"
            );

            given(s3Service.generatePresignedUrl("한글파일명.jpg", "image/jpeg")).willReturn(response);

            mockMvc.perform(post("/api/files/presigned-url")
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.presignedUrl").exists())
                   .andExpect(jsonPath("$.s3Url").exists());
        }
    }
}
