package com.project.codelight.comment.service;

import com.project.codelight.comment.repository.CommentLikeRepository;
import com.project.codelight.comment.repository.CommentRepository;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.post.domain.Comment;
import com.project.codelight.post.domain.CommentLike;
import com.project.codelight.user.domain.User;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public boolean toggleLike(Long commentId, User user) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                                           .orElseThrow(() -> new CodeLightException(
                                               ExceptionCodeType.COMMENT_NOT_FOUND));

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment,
            user);

        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            comment.decreaseLikes();
            commentRepository.save(comment);
            return false;
        } else {
            CommentLike newLike = CommentLike.builder()
                                             .comment(comment)
                                             .user(user)
                                             .build();
            commentLikeRepository.save(newLike);
            comment.increaseLikes();
            commentRepository.save(comment);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long commentId, User user) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                                           .orElseThrow(() -> new CodeLightException(
                                               ExceptionCodeType.COMMENT_NOT_FOUND));
        return commentLikeRepository.existsByCommentAndUser(comment, user);
    }

    @Transactional(readOnly = true)
    public Set<Long> getLikedCommentIds(List<Long> commentIds, User user) {
        if (user == null || commentIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(
            commentLikeRepository.findLikedCommentIdsByUserAndCommentIds(user, commentIds));
    }
}
