package com.project.codelight.post.controller;

import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @GetMapping("/api/posts")
    public ResponseEntity<PostResponses> getPosts(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<Post> posts = postService.getActivePosts();
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        User user = userDetails != null ? userDetails.getUser() : null;
        Set<Long> likedPostIds = postLikeService.getLikedPostIds(postIds, user);

        List<PostSummaryResponse> postList = posts.stream()
                                                  .map(post -> PostSummaryResponse.from(
                                                      post, likedPostIds.contains(post.getId())))
                                                  .toList();
        return ResponseEntity.ok(new PostResponses(postList));
    }

    @GetMapping("/api/post/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = postService.getActivePostById(postId)
                               .orElseThrow(
                                   () -> new CodeLightException(ExceptionCodeType.POST_NOT_FOUND));

        boolean liked = userDetails != null
            && postLikeService.isLikedByUser(postId, userDetails.getUser());

        PostDetailResponse postDetailResponse = PostDetailResponse.from(post, userDetails, liked);
        return ResponseEntity.ok(postDetailResponse);
    }

    @PostMapping("/api/post")
    public ResponseEntity<Void> createPost(
        @Valid @RequestBody PostCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        Post newPost = Post.builder()
                           .user(user)
                           .content(request.content())
                           .build();

        Post saved;
        if (request.poll() != null) {
            saved = postService.createPostWithFilesAndPoll(newPost, request.files(),
                request.poll());
        } else {
            saved = postService.createPostWithFiles(newPost, request.files());
        }

        return ResponseEntity.created(URI.create("/api/posts/" + saved.getId())).build();
    }

    @PutMapping("/api/post/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId,
                                           @Valid @RequestBody PostUpdateRequest request,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        postService.updatePostContent(postId, request.content(), request.files(), request.poll(),
            user);
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
