package com.project.codelight.post.controller;

import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.global.config.CloudFrontProperties;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.poll.domain.Poll;
import com.project.codelight.poll.service.PollService;
import com.project.codelight.post.domain.Post;
import com.project.codelight.post.dto.request.PostCreateRequest;
import com.project.codelight.post.dto.request.PostUpdateRequest;
import com.project.codelight.post.dto.response.PostDetailResponse;
import com.project.codelight.post.dto.response.PostResponses;
import com.project.codelight.post.dto.response.PostSummaryResponse;
import com.project.codelight.post.service.PostLikeService;
import com.project.codelight.post.service.PostService;
import com.project.codelight.user.domain.User;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final PollService pollService;
    private final CloudFrontProperties cloudFrontProperties;

    private static final int FIRST_PAGE_SIZE = 5;
    private static final int DEFAULT_PAGE_SIZE = 5;

    @GetMapping("/api/posts")
    public ResponseEntity<PostResponses> getPosts(
        @RequestParam(required = false) Long lastPostId,
        @RequestParam(required = false) String keyword,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<Post> posts = postService.getActivePosts(lastPostId, keyword);

        int pageSize = (lastPostId == null) ? FIRST_PAGE_SIZE : DEFAULT_PAGE_SIZE;
        boolean hasNext = posts.size() > pageSize;

        if (hasNext) {
            posts = posts.subList(0, pageSize);
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        User user = userDetails != null ? userDetails.getUser() : null;
        Set<Long> likedPostIds = postLikeService.getLikedPostIds(postIds, user);

        List<PostSummaryResponse> postList = posts.stream()
                                                  .map(post -> {
                                                      Set<Long> votedOptionIds = Set.of();
                                                      boolean hasVotedPoll = false;

                                                      Poll poll = post.getPoll();
                                                      if (poll != null && user != null) {
                                                          votedOptionIds = pollService.getVotedOptionIds(poll, user);
                                                          hasVotedPoll = pollService.hasUserVoted(poll, user);
                                                      }

                                                      return PostSummaryResponse.from(
                                                          post,
                                                          likedPostIds.contains(post.getId()),
                                                          votedOptionIds,
                                                          hasVotedPoll,
                                                          cloudFrontProperties.domain()
                                                      );
                                                  })
                                                  .toList();
        return ResponseEntity.ok(new PostResponses(postList, hasNext));
    }

    @GetMapping("/api/post/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = postService.getActivePostById(postId)
                               .orElseThrow(
                                   () -> new CodeLightException(ExceptionCodeType.POST_NOT_FOUND));

        User user = userDetails != null ? userDetails.getUser() : null;

        boolean liked = user != null && postLikeService.isLikedByUser(postId, user);

        Set<Long> votedOptionIds = Set.of();
        boolean hasVotedPoll = false;

        Poll poll = post.getPoll();
        if (poll != null && user != null) {
            votedOptionIds = pollService.getVotedOptionIds(poll, user);
            hasVotedPoll = pollService.hasUserVoted(poll, user);
        }

        PostDetailResponse postDetailResponse = PostDetailResponse.from(
            post, userDetails, liked, votedOptionIds, hasVotedPoll, cloudFrontProperties.domain());
        return ResponseEntity.ok(postDetailResponse);
    }

    @PostMapping(value = "/api/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPost(
        @Valid @RequestPart("request") PostCreateRequest request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        Post saved = postService.createPost(user, request, files);

        return ResponseEntity.created(URI.create("/api/posts/" + saved.getId())).build();
    }

    @PutMapping(value = "/api/post/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePost(
        @PathVariable Long postId,
        @Valid @RequestPart("request") PostUpdateRequest request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        postService.updatePost(postId, user, request, files);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/post/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        postService.softDeletePost(postId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/post/{postId}/restore")
    public ResponseEntity<Void> restorePost(@PathVariable Long postId) {
        postService.restorePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/post/{postId}/like")
    public ResponseEntity<LikeResponse> toggleLike(@PathVariable Long postId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        boolean liked = postLikeService.toggleLike(postId, user);
        return ResponseEntity.ok(new LikeResponse(liked));
    }

    public record LikeResponse(boolean liked) {
    }
}
