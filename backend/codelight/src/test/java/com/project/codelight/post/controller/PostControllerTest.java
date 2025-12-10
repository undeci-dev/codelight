package com.project.codelight.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.global.config.CloudFrontProperties;
import com.project.codelight.poll.service.PollService;
import com.project.codelight.post.domain.Post;
import com.project.codelight.post.dto.request.PostCreateRequest;
import com.project.codelight.post.dto.request.PostUpdateRequest;
import com.project.codelight.post.service.PostLikeService;
import com.project.codelight.post.service.PostService;
import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import com.project.codelight.user.domain.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.project.codelight.config.TestSecurityConfig;
import com.project.codelight.auth.config.WebSecurityConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(controllers = PostController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)
})
@Import(TestSecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private PostLikeService postLikeService;

    @MockitoBean
    private PollService pollService;

    @MockitoBean
    private CloudFrontProperties cloudFrontProperties;

    @MockitoBean
    private com.project.codelight.auth.repository.TokenBlackListRepository tokenBlackListRepository;

    private User testUser;
    private CustomUserDetails userDetails;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                       .id(1L)
                       .name("testUser")
                       .email("test@example.com")
                       .password("password")
                       .userRole(UserRole.USER)
                       .loginType(LoginType.LOCAL)
                       .build();

        userDetails = new CustomUserDetails(
            testUser,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        testPost = Post.builder()
                       .user(testUser)
                       .content("테스트 게시글 내용입니다.")
                       .likesCount(0)
                       .commentsCount(0)
                       .sharesCount(0)
                       .build();
        org.springframework.test.util.ReflectionTestUtils.setField(testPost, "id", 1L);

        given(cloudFrontProperties.domain()).willReturn("test.cloudfront.net");
    }

    private void setAuthentication() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Nested
    @DisplayName("게시글 목록 조회 API")
    class GetPostsTest {

        @Test
        @DisplayName("로그인한 사용자가 게시글 목록을 조회한다")
        void getPostsWithAuthentication() throws Exception {
            given(postService.getActivePosts(any(), any())).willReturn(List.of(testPost));
            given(postLikeService.getLikedPostIds(anyList(), any(User.class)))
                .willReturn(Set.of());

            mockMvc.perform(get("/api/posts")
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.posts").isArray())
                   .andExpect(jsonPath("$.hasNext").isBoolean());
        }

        @Test
        @DisplayName("비로그인 사용자도 게시글 목록을 조회할 수 있다")
        void getPostsWithoutAuthentication() throws Exception {
            given(postService.getActivePosts(any(), any())).willReturn(List.of(testPost));
            given(postLikeService.getLikedPostIds(anyList(), any()))
                .willReturn(Set.of());

            mockMvc.perform(get("/api/posts")
                       .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.posts").isArray())
                   .andExpect(jsonPath("$.hasNext").isBoolean());
        }

        @Test
        @DisplayName("lastPostId를 전달하면 해당 게시글 이후의 목록을 조회한다")
        void getPostsWithLastPostId() throws Exception {
            given(postService.getActivePosts(eq(10L), any())).willReturn(List.of(testPost));
            given(postLikeService.getLikedPostIds(anyList(), any(User.class)))
                .willReturn(Set.of());

            mockMvc.perform(get("/api/posts")
                       .param("lastPostId", "10")
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.posts").isArray())
                   .andExpect(jsonPath("$.hasNext").isBoolean());
        }

        @Test
        @DisplayName("keyword로 게시글을 검색한다")
        void getPostsWithKeyword() throws Exception {
            given(postService.getActivePosts(any(), eq("테스트"))).willReturn(List.of(testPost));
            given(postLikeService.getLikedPostIds(anyList(), any(User.class)))
                .willReturn(Set.of());

            mockMvc.perform(get("/api/posts")
                       .param("keyword", "테스트")
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.posts").isArray())
                   .andExpect(jsonPath("$.hasNext").isBoolean());
        }
    }

    @Nested
    @DisplayName("게시글 단건 조회 API")
    class GetPostTest {

        @Test
        @DisplayName("로그인한 사용자가 게시글을 조회한다")
        void getPostWithAuthentication() throws Exception {
            given(postService.getActivePostById(1L)).willReturn(Optional.of(testPost));
            given(postLikeService.isLikedByUser(anyLong(), any(User.class))).willReturn(false);

            mockMvc.perform(get("/api/post/{postId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content").value("테스트 게시글 내용입니다."))
                   .andExpect(jsonPath("$.userName").value("testUser"));
        }

        @Test
        @DisplayName("존재하지 않는 게시글 조회 시 404를 반환한다")
        void getPostNotFound() throws Exception {
            given(postService.getActivePostById(999L)).willReturn(Optional.empty());

            mockMvc.perform(get("/api/post/{postId}", 999L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("게시글 생성 API")
    class CreatePostTest {

        @Test
        @DisplayName("게시글을 성공적으로 생성한다")
        void createPostSuccess() throws Exception {
            PostCreateRequest request = new PostCreateRequest("새로운 게시글 내용", null, null);

            Post savedPost = Post.builder()
                                 .user(testUser)
                                 .content("새로운 게시글 내용")
                                 .build();

            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(postService.createPost(any(User.class), any(PostCreateRequest.class), any()))
                .willReturn(savedPost);

            mockMvc.perform(multipart("/api/post")
                       .file(requestPart)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andDo(print())
                   .andExpect(status().isCreated())
                   .andExpect(header().exists("Location"));
        }

        @Test
        @DisplayName("파일과 함께 게시글을 생성한다")
        void createPostWithFiles() throws Exception {
            PostCreateRequest request = new PostCreateRequest("파일이 있는 게시글", null, null);

            Post savedPost = Post.builder()
                                 .user(testUser)
                                 .content("파일이 있는 게시글")
                                 .build();

            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            MockMultipartFile filePart = new MockMultipartFile(
                "files",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
            );

            given(postService.createPost(any(User.class), any(PostCreateRequest.class), anyList()))
                .willReturn(savedPost);

            mockMvc.perform(multipart("/api/post")
                       .file(requestPart)
                       .file(filePart)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andDo(print())
                   .andExpect(status().isCreated())
                   .andExpect(header().exists("Location"));
        }

        @Test
        @DisplayName("내용이 비어있으면 400을 반환한다")
        void createPostWithEmptyContent() throws Exception {
            PostCreateRequest request = new PostCreateRequest("", null, null);

            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/post")
                       .file(requestPart)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("비로그인 사용자는 게시글을 생성할 수 없다")
        void createPostUnauthorized() throws Exception {
            PostCreateRequest request = new PostCreateRequest("새로운 게시글", null, null);

            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/post")
                       .file(requestPart)
                       .with(SecurityMockMvcRequestPostProcessors.anonymous())
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andDo(print())
                   .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("게시글 수정 API")
    class UpdatePostTest {

        @Test
        @DisplayName("게시글을 성공적으로 수정한다")
        void updatePostSuccess() throws Exception {
            PostUpdateRequest request = new PostUpdateRequest(
                "수정된 내용", null, null, null, null, null
            );

            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            doNothing().when(postService).updatePost(anyLong(), any(User.class),
                any(PostUpdateRequest.class), any());

            mockMvc.perform(multipart("/api/post/{postId}", 1L)
                       .file(requestPart)
                       .with(req -> {
                           req.setMethod("PUT");
                           return req;
                       })
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andDo(print())
                   .andExpect(status().isNoContent());

            verify(postService).updatePost(eq(1L), any(User.class),
                any(PostUpdateRequest.class), any());
        }

        @Test
        @DisplayName("파일 삭제와 순서 변경을 포함한 수정")
        void updatePostWithFileChanges() throws Exception {
            PostUpdateRequest request = new PostUpdateRequest(
                "수정된 내용",
                null,
                null,
                List.of(1L, 2L),  // deleteFileIds
                List.of(new com.project.codelight.post.dto.request.FileOrderUpdate(3L, 0)),  // fileOrders
                null
            );

            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            doNothing().when(postService).updatePost(anyLong(), any(User.class),
                any(PostUpdateRequest.class), any());

            mockMvc.perform(multipart("/api/post/{postId}", 1L)
                       .file(requestPart)
                       .with(req -> {
                           req.setMethod("PUT");
                           return req;
                       })
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andDo(print())
                   .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("수정할 내용이 비어있으면 400을 반환한다")
        void updatePostWithEmptyContent() throws Exception {
            PostUpdateRequest request = new PostUpdateRequest("", null, null, null, null, null);

            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/post/{postId}", 1L)
                       .file(requestPart)
                       .with(req -> {
                           req.setMethod("PUT");
                           return req;
                       })
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("게시글 삭제 API")
    class DeletePostTest {

        @Test
        @DisplayName("게시글을 성공적으로 삭제한다")
        void deletePostSuccess() throws Exception {
            doNothing().when(postService).softDeletePost(anyLong(), any(User.class));

            mockMvc.perform(delete("/api/post/{postId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isNoContent());

            verify(postService).softDeletePost(eq(1L), any(User.class));
        }
    }

    @Nested
    @DisplayName("게시글 복원 API")
    class RestorePostTest {

        @Test
        @DisplayName("삭제된 게시글을 복원한다")
        void restorePostSuccess() throws Exception {
            doNothing().when(postService).restorePost(1L);

            mockMvc.perform(post("/api/post/{postId}/restore", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isNoContent());

            verify(postService).restorePost(1L);
        }
    }

    @Nested
    @DisplayName("좋아요 토글 API")
    class ToggleLikeTest {

        @Test
        @DisplayName("좋아요를 성공적으로 토글한다 - 좋아요 추가")
        void toggleLikeAdd() throws Exception {
            given(postLikeService.toggleLike(anyLong(), any(User.class))).willReturn(true);

            mockMvc.perform(post("/api/post/{postId}/like", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.liked").value(true));
        }

        @Test
        @DisplayName("좋아요를 성공적으로 토글한다 - 좋아요 취소")
        void toggleLikeRemove() throws Exception {
            given(postLikeService.toggleLike(anyLong(), any(User.class))).willReturn(false);

            mockMvc.perform(post("/api/post/{postId}/like", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.liked").value(false));
        }

        @Test
        @DisplayName("비로그인 사용자는 좋아요를 할 수 없다")
        void toggleLikeUnauthorized() throws Exception {
            mockMvc.perform(post("/api/post/{postId}/like", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.anonymous())
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isUnauthorized());
        }
    }
}
