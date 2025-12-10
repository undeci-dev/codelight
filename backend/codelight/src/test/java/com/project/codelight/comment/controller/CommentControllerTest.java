package com.project.codelight.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.comment.domain.Comment;
import com.project.codelight.comment.dto.request.CommentCreateRequest;
import com.project.codelight.comment.dto.request.CommentUpdateRequest;
import com.project.codelight.comment.service.CommentLikeService;
import com.project.codelight.comment.service.CommentService;
import com.project.codelight.post.domain.Post;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.project.codelight.config.TestSecurityConfig;
import com.project.codelight.auth.config.WebSecurityConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(controllers = CommentController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)
})
@Import(TestSecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private CommentLikeService commentLikeService;

    @MockitoBean
    private com.project.codelight.auth.repository.TokenBlackListRepository tokenBlackListRepository;

    private User testUser;
    private CustomUserDetails userDetails;
    private Post testPost;
    private Comment testComment;

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
                       .content("테스트 게시글")
                       .build();
        org.springframework.test.util.ReflectionTestUtils.setField(testPost, "id", 1L);

        testComment = Comment.builder()
                             .post(testPost)
                             .user(testUser)
                             .content("테스트 댓글입니다.")
                             .likesCount(0)
                             .build();
        org.springframework.test.util.ReflectionTestUtils.setField(testComment, "id", 1L);
    }

    @Nested
    @DisplayName("댓글 목록 조회 API")
    class GetCommentsTest {

        @Test
        @DisplayName("로그인한 사용자가 게시글의 댓글 목록을 조회한다")
        void getCommentsWithAuthentication() throws Exception {
            given(commentService.getCommentsByPost(1L)).willReturn(List.of(testComment));
            given(commentLikeService.getLikedCommentIds(anyList(), any(User.class)))
                .willReturn(Set.of());

            mockMvc.perform(get("/api/post/{postId}/comments", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.comments").isArray())
                   .andExpect(jsonPath("$.totalCount").value(1));
        }

        @Test
        @DisplayName("비로그인 사용자도 댓글 목록을 조회할 수 있다")
        void getCommentsWithoutAuthentication() throws Exception {
            given(commentService.getCommentsByPost(1L)).willReturn(List.of(testComment));
            given(commentLikeService.getLikedCommentIds(anyList(), any()))
                .willReturn(Set.of());

            mockMvc.perform(get("/api/post/{postId}/comments", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.comments").isArray());
        }
    }

    @Nested
    @DisplayName("댓글 단건 조회 API")
    class GetCommentTest {

        @Test
        @DisplayName("로그인한 사용자가 댓글을 조회한다")
        void getCommentWithAuthentication() throws Exception {
            given(commentService.getCommentById(1L)).willReturn(Optional.of(testComment));
            given(commentLikeService.isLikedByUser(anyLong(), any(User.class))).willReturn(false);

            mockMvc.perform(get("/api/comment/{commentId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content").value("테스트 댓글입니다."));
        }

        @Test
        @DisplayName("존재하지 않는 댓글 조회 시 404를 반환한다")
        void getCommentNotFound() throws Exception {
            given(commentService.getCommentById(999L)).willReturn(Optional.empty());

            mockMvc.perform(get("/api/comment/{commentId}", 999L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("댓글 생성 API")
    class CreateCommentTest {

        @Test
        @DisplayName("댓글을 성공적으로 생성한다")
        void createCommentSuccess() throws Exception {
            CommentCreateRequest request = new CommentCreateRequest("새로운 댓글", null);

            given(commentService.createComment(anyLong(), any(), any(), any(User.class)))
                .willReturn(testComment);

            mockMvc.perform(post("/api/post/{postId}/comment", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isCreated())
                   .andExpect(header().exists("Location"));
        }

        @Test
        @DisplayName("대댓글을 성공적으로 생성한다")
        void createReplySuccess() throws Exception {
            CommentCreateRequest request = new CommentCreateRequest("대댓글입니다", 1L);

            given(commentService.createComment(anyLong(), any(), eq(1L), any(User.class)))
                .willReturn(testComment);

            mockMvc.perform(post("/api/post/{postId}/comment", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("내용이 비어있으면 400을 반환한다")
        void createCommentWithEmptyContent() throws Exception {
            CommentCreateRequest request = new CommentCreateRequest("", null);

            mockMvc.perform(post("/api/post/{postId}/comment", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("비로그인 사용자는 댓글을 생성할 수 없다")
        void createCommentUnauthorized() throws Exception {
            CommentCreateRequest request = new CommentCreateRequest("새로운 댓글", null);

            mockMvc.perform(post("/api/post/{postId}/comment", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.anonymous())
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("댓글 수정 API")
    class UpdateCommentTest {

        @Test
        @DisplayName("댓글을 성공적으로 수정한다")
        void updateCommentSuccess() throws Exception {
            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

            doNothing().when(commentService).updateComment(anyLong(), any(), any(User.class));

            mockMvc.perform(put("/api/comment/{commentId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isNoContent());

            verify(commentService).updateComment(eq(1L), eq("수정된 댓글"), any(User.class));
        }

        @Test
        @DisplayName("수정할 내용이 비어있으면 400을 반환한다")
        void updateCommentWithEmptyContent() throws Exception {
            CommentUpdateRequest request = new CommentUpdateRequest("");

            mockMvc.perform(put("/api/comment/{commentId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("댓글 삭제 API")
    class DeleteCommentTest {

        @Test
        @DisplayName("댓글을 성공적으로 삭제한다")
        void deleteCommentSuccess() throws Exception {
            doNothing().when(commentService).deleteComment(anyLong(), any(User.class));

            mockMvc.perform(delete("/api/comment/{commentId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isNoContent());

            verify(commentService).deleteComment(eq(1L), any(User.class));
        }
    }

    @Nested
    @DisplayName("댓글 좋아요 토글 API")
    class ToggleLikeTest {

        @Test
        @DisplayName("좋아요를 성공적으로 토글한다 - 좋아요 추가")
        void toggleLikeAdd() throws Exception {
            given(commentLikeService.toggleLike(anyLong(), any(User.class))).willReturn(true);

            mockMvc.perform(post("/api/comment/{commentId}/like", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.liked").value(true));
        }

        @Test
        @DisplayName("좋아요를 성공적으로 토글한다 - 좋아요 취소")
        void toggleLikeRemove() throws Exception {
            given(commentLikeService.toggleLike(anyLong(), any(User.class))).willReturn(false);

            mockMvc.perform(post("/api/comment/{commentId}/like", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.liked").value(false));
        }

        @Test
        @DisplayName("비로그인 사용자는 좋아요를 할 수 없다")
        void toggleLikeUnauthorized() throws Exception {
            mockMvc.perform(post("/api/comment/{commentId}/like", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.anonymous())
                       .with(SecurityMockMvcRequestPostProcessors.csrf()))
                   .andDo(print())
                   .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("대댓글 목록 조회 API")
    class GetRepliesTest {

        @Test
        @DisplayName("대댓글 목록을 조회한다")
        void getRepliesSuccess() throws Exception {
            Comment reply = Comment.builder()
                                   .post(testPost)
                                   .user(testUser)
                                   .parent(testComment)
                                   .content("대댓글입니다")
                                   .build();
            org.springframework.test.util.ReflectionTestUtils.setField(reply, "id", 2L);

            given(commentService.getRepliesByParent(1L)).willReturn(List.of(reply));
            given(commentLikeService.getLikedCommentIds(anyList(), any()))
                .willReturn(Set.of());

            mockMvc.perform(get("/api/comment/{parentId}/replies", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.comments").isArray())
                   .andExpect(jsonPath("$.totalCount").value(1));
        }
    }
}
