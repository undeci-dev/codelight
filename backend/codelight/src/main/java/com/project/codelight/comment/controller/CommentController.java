package com.project.codelight.comment.controller;

import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.comment.domain.Comment;
import com.project.codelight.comment.dto.request.CommentCreateRequest;
import com.project.codelight.comment.dto.request.CommentUpdateRequest;
import com.project.codelight.comment.dto.response.CommentResponse;
import com.project.codelight.comment.dto.response.CommentResponses;
import com.project.codelight.comment.service.CommentLikeService;
import com.project.codelight.comment.service.CommentService;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.User;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
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
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @GetMapping("/api/post/{postId}/comments")
    public ResponseEntity<CommentResponses> getComments(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<Comment> rootComments = commentService.getCommentsByPost(postId);

        User user = userDetails != null ? userDetails.getUser() : null;
        Long currentUserId = user != null ? user.getId() : null;

        List<Long> allCommentIds = collectAllCommentIds(rootComments);
        Set<Long> likedCommentIds = commentLikeService.getLikedCommentIds(allCommentIds, user);

        List<CommentResponse> commentResponses = rootComments.stream()
            .map(comment -> toCommentResponse(comment, currentUserId, likedCommentIds))
            .toList();

        int totalCount = allCommentIds.size();
        return ResponseEntity.ok(new CommentResponses(commentResponses, totalCount));
    }

    @GetMapping("/api/comment/{commentId}")
    public ResponseEntity<CommentResponse> getComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        Comment comment = commentService.getCommentById(commentId)
                                        .orElseThrow(() -> new CodeLightException(
                                            ExceptionCodeType.COMMENT_NOT_FOUND));

        User user = userDetails != null ? userDetails.getUser() : null;
        Long currentUserId = user != null ? user.getId() : null;
        boolean liked = user != null && commentLikeService.isLikedByUser(commentId, user);

        return ResponseEntity.ok(CommentResponse.from(comment, currentUserId, liked));
    }

    @PostMapping("/api/post/{postId}/comment")
    public ResponseEntity<CommentResponse> createComment(
        @PathVariable Long postId,
        @Valid @RequestBody CommentCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        Comment savedComment = commentService.createComment(
            postId,
            request.content(),
            request.parentId(),
            user
        );

        CommentResponse response = CommentResponse.from(savedComment, user.getId(), false);
        return ResponseEntity
            .created(URI.create("/api/comment/" + savedComment.getId()))
            .body(response);
    }

    @PutMapping("/api/comment/{commentId}")
    public ResponseEntity<Void> updateComment(
        @PathVariable Long commentId,
        @Valid @RequestBody CommentUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        commentService.updateComment(commentId, request.content(), user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/comment/{commentId}/like")
    public ResponseEntity<LikeResponse> toggleLike(
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        boolean liked = commentLikeService.toggleLike(commentId, user);
        return ResponseEntity.ok(new LikeResponse(liked));
    }

    @GetMapping("/api/comment/{parentId}/replies")
    public ResponseEntity<CommentResponses> getReplies(
        @PathVariable Long parentId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<Comment> replies = commentService.getRepliesByParent(parentId);

        User user = userDetails != null ? userDetails.getUser() : null;
        Long currentUserId = user != null ? user.getId() : null;

        List<Long> replyIds = replies.stream().map(Comment::getId).toList();
        Set<Long> likedCommentIds = commentLikeService.getLikedCommentIds(replyIds, user);

        List<CommentResponse> replyResponses = replies.stream()
            .map(reply -> CommentResponse.from(reply, currentUserId,
                likedCommentIds.contains(reply.getId())))
            .toList();

        return ResponseEntity.ok(new CommentResponses(replyResponses, replies.size()));
    }

    private List<Long> collectAllCommentIds(List<Comment> comments) {
        List<Long> ids = new ArrayList<>();
        for (Comment comment : comments) {
            ids.add(comment.getId());
            if (comment.getChildren() != null && !comment.getChildren().isEmpty()) {
                ids.addAll(collectAllCommentIds(new ArrayList<>(comment.getChildren())));
            }
        }
        return ids;
    }

    private CommentResponse toCommentResponse(Comment comment, Long currentUserId,
                                              Set<Long> likedCommentIds) {
        List<CommentResponse> replyResponses = List.of();

        if (comment.getChildren() != null && !comment.getChildren().isEmpty()) {
            replyResponses = comment.getChildren().stream()
                                    .filter(child -> !child.isDeleted())
                                    .map(child -> toCommentResponse(child, currentUserId,
                                        likedCommentIds))
                                    .toList();
        }

        return CommentResponse.from(
            comment,
            currentUserId,
            likedCommentIds.contains(comment.getId()),
            replyResponses
        );
    }

    public record LikeResponse(boolean liked) {
    }
}
