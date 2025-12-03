package com.project.codelight.poll.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.poll.domain.Poll;
import com.project.codelight.poll.domain.PollOption;
import com.project.codelight.poll.dto.request.PollVoteRequest;
import com.project.codelight.poll.service.PollService;
import com.project.codelight.post.domain.Post;
import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import com.project.codelight.user.domain.UserRole;
import java.time.LocalDateTime;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PollController.class)
class PollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PollService pollService;

    private User testUser;
    private CustomUserDetails userDetails;
    private Post testPost;
    private Poll testPoll;
    private List<PollOption> testOptions;

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

        testPoll = Poll.builder()
                       .post(testPost)
                       .question("좋아하는 프로그래밍 언어는?")
                       .multipleChoice(false)
                       .endsAt(LocalDateTime.now().plusDays(7))
                       .totalVotes(0)
                       .build();

        testOptions = List.of(
            PollOption.builder()
                      .poll(testPoll)
                      .optionText("Java")
                      .votesCount(0)
                      .displayOrder(0)
                      .build(),
            PollOption.builder()
                      .poll(testPoll)
                      .optionText("Python")
                      .votesCount(0)
                      .displayOrder(1)
                      .build()
        );
    }

    @Nested
    @DisplayName("게시글별 투표 조회 API")
    class GetPollByPostTest {

        @Test
        @DisplayName("로그인한 사용자가 게시글의 투표를 조회한다")
        @WithMockUser
        void getPollByPostWithAuthentication() throws Exception {
            given(pollService.getPollByPostId(1L)).willReturn(Optional.of(testPoll));
            given(pollService.getPollOptions(testPoll)).willReturn(testOptions);
            given(pollService.getVotedOptionIds(any(Poll.class), any(User.class)))
                .willReturn(Set.of());
            given(pollService.hasUserVoted(any(Poll.class), any(User.class))).willReturn(false);

            mockMvc.perform(get("/api/post/{postId}/poll", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.question").value("좋아하는 프로그래밍 언어는?"))
                   .andExpect(jsonPath("$.options").isArray())
                   .andExpect(jsonPath("$.options.length()").value(2));
        }

        @Test
        @DisplayName("비로그인 사용자도 투표를 조회할 수 있다")
        void getPollByPostWithoutAuthentication() throws Exception {
            given(pollService.getPollByPostId(1L)).willReturn(Optional.of(testPoll));
            given(pollService.getPollOptions(testPoll)).willReturn(testOptions);
            given(pollService.getVotedOptionIds(any(Poll.class), any()))
                .willReturn(Set.of());
            given(pollService.hasUserVoted(any(Poll.class), any())).willReturn(false);

            mockMvc.perform(get("/api/post/{postId}/poll", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.question").value("좋아하는 프로그래밍 언어는?"));
        }

        @Test
        @DisplayName("존재하지 않는 투표 조회 시 404를 반환한다")
        @WithMockUser
        void getPollByPostNotFound() throws Exception {
            given(pollService.getPollByPostId(999L)).willReturn(Optional.empty());

            mockMvc.perform(get("/api/post/{postId}/poll", 999L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("투표 단건 조회 API")
    class GetPollTest {

        @Test
        @DisplayName("로그인한 사용자가 투표를 조회한다")
        @WithMockUser
        void getPollWithAuthentication() throws Exception {
            given(pollService.getPollById(1L)).willReturn(testPoll);
            given(pollService.getPollOptions(testPoll)).willReturn(testOptions);
            given(pollService.getVotedOptionIds(any(Poll.class), any(User.class)))
                .willReturn(Set.of());
            given(pollService.hasUserVoted(any(Poll.class), any(User.class))).willReturn(false);

            mockMvc.perform(get("/api/poll/{pollId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.question").value("좋아하는 프로그래밍 언어는?"))
                   .andExpect(jsonPath("$.multipleChoice").value(false));
        }

        @Test
        @DisplayName("비로그인 사용자도 투표를 조회할 수 있다")
        void getPollWithoutAuthentication() throws Exception {
            given(pollService.getPollById(1L)).willReturn(testPoll);
            given(pollService.getPollOptions(testPoll)).willReturn(testOptions);
            given(pollService.getVotedOptionIds(any(Poll.class), any()))
                .willReturn(Set.of());
            given(pollService.hasUserVoted(any(Poll.class), any())).willReturn(false);

            mockMvc.perform(get("/api/poll/{pollId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.question").value("좋아하는 프로그래밍 언어는?"));
        }
    }

    @Nested
    @DisplayName("투표하기 API")
    class VoteTest {

        @Test
        @DisplayName("투표를 성공적으로 한다")
        @WithMockUser
        void voteSuccess() throws Exception {
            PollVoteRequest request = new PollVoteRequest(List.of(1L));

            doNothing().when(pollService).vote(anyLong(), anyList(), any(User.class));
            given(pollService.getPollById(1L)).willReturn(testPoll);
            given(pollService.getPollOptions(testPoll)).willReturn(testOptions);
            given(pollService.getVotedOptionIds(any(Poll.class), any(User.class)))
                .willReturn(Set.of(1L));
            given(pollService.hasUserVoted(any(Poll.class), any(User.class))).willReturn(true);

            mockMvc.perform(post("/api/poll/{pollId}/vote", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.hasVoted").value(true));

            verify(pollService).vote(eq(1L), eq(List.of(1L)), any(User.class));
        }

        @Test
        @DisplayName("복수 선택 투표를 성공적으로 한다")
        @WithMockUser
        void voteMultipleSuccess() throws Exception {
            PollVoteRequest request = new PollVoteRequest(List.of(1L, 2L));

            Poll multiPoll = Poll.builder()
                                 .post(testPost)
                                 .question("좋아하는 언어를 모두 선택하세요")
                                 .multipleChoice(true)
                                 .totalVotes(0)
                                 .build();

            doNothing().when(pollService).vote(anyLong(), anyList(), any(User.class));
            given(pollService.getPollById(1L)).willReturn(multiPoll);
            given(pollService.getPollOptions(multiPoll)).willReturn(testOptions);
            given(pollService.getVotedOptionIds(any(Poll.class), any(User.class)))
                .willReturn(Set.of(1L, 2L));
            given(pollService.hasUserVoted(any(Poll.class), any(User.class))).willReturn(true);

            mockMvc.perform(post("/api/poll/{pollId}/vote", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.hasVoted").value(true));
        }

        @Test
        @DisplayName("옵션을 선택하지 않으면 400을 반환한다")
        @WithMockUser
        void voteWithEmptyOptions() throws Exception {
            PollVoteRequest request = new PollVoteRequest(List.of());

            mockMvc.perform(post("/api/poll/{pollId}/vote", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("비로그인 사용자는 투표할 수 없다")
        void voteUnauthorized() throws Exception {
            PollVoteRequest request = new PollVoteRequest(List.of(1L));

            mockMvc.perform(post("/api/poll/{pollId}/vote", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.anonymous())
                       .with(SecurityMockMvcRequestPostProcessors.csrf())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
                   .andDo(print())
                   .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("투표 응답 검증")
    class PollResponseTest {

        @Test
        @DisplayName("투표한 사용자의 응답에는 hasVoted가 true이다")
        @WithMockUser
        void pollResponseWithVotedUser() throws Exception {
            given(pollService.getPollById(1L)).willReturn(testPoll);
            given(pollService.getPollOptions(testPoll)).willReturn(testOptions);
            given(pollService.getVotedOptionIds(any(Poll.class), any(User.class)))
                .willReturn(Set.of(1L));
            given(pollService.hasUserVoted(any(Poll.class), any(User.class))).willReturn(true);

            mockMvc.perform(get("/api/poll/{pollId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.hasVoted").value(true));
        }

        @Test
        @DisplayName("투표하지 않은 사용자의 응답에는 hasVoted가 false이다")
        @WithMockUser
        void pollResponseWithNotVotedUser() throws Exception {
            given(pollService.getPollById(1L)).willReturn(testPoll);
            given(pollService.getPollOptions(testPoll)).willReturn(testOptions);
            given(pollService.getVotedOptionIds(any(Poll.class), any(User.class)))
                .willReturn(Set.of());
            given(pollService.hasUserVoted(any(Poll.class), any(User.class))).willReturn(false);

            mockMvc.perform(get("/api/poll/{pollId}", 1L)
                       .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.hasVoted").value(false));
        }
    }
}
