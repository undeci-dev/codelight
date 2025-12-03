package com.project.codelight.comment.dto.response;

import com.project.codelight.post.domain.Comment;
import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
    Long commentId,
    Long userId,
    String userName,
    String content,
    int likesCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isOwner,
    boolean liked,
    Long parentId,
    List<CommentResponse> replies
) {

    public static CommentResponse from(Comment comment, Long currentUserId, boolean liked,
                                       List<CommentResponse> replies) {
        boolean isOwner = currentUserId != null
            && comment.getUser() != null
            && comment.getUser().getId().equals(currentUserId);

        return new CommentResponse(
            comment.getId(),
            comment.getUser().getId(),
            comment.getUser().getName(),
            comment.getContent(),
            comment.getLikesCount(),
            comment.getCreatedAt(),
            comment.getUpdatedAt(),
            isOwner,
            liked,
            comment.getParent() != null ? comment.getParent().getId() : null,
            replies
        );
    }

    public static CommentResponse from(Comment comment, Long currentUserId, boolean liked) {
        return from(comment, currentUserId, liked, List.of());
    }
}
