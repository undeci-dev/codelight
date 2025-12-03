package com.project.codelight.comment.service;

import com.project.codelight.comment.domain.Comment;
import com.project.codelight.comment.repository.CommentRepository;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.post.domain.Post;
import com.project.codelight.post.repository.PostRepository;
import com.project.codelight.user.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Long postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.POST_NOT_FOUND));
        return commentRepository.findRootCommentsWithRepliesByPost(post);
    }

    @Transactional(readOnly = true)
    public List<Comment> getRepliesByParent(Long parentId) {
        Comment parent = commentRepository.findByIdAndDeletedFalse(parentId)
                                          .orElseThrow(() -> new CodeLightException(
                                              ExceptionCodeType.COMMENT_NOT_FOUND));
        return commentRepository.findRepliesByParent(parent);
    }

    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findByIdAndDeletedFalse(commentId);
    }

    @Transactional
    public Comment createComment(Long postId, String content, Long parentId, User user) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.POST_NOT_FOUND));

        Comment.CommentBuilder builder = Comment.builder()
                                                .post(post)
                                                .user(user)
                                                .content(content);

        if (parentId != null) {
            Comment parent = commentRepository.findByIdAndDeletedFalse(parentId)
                                              .orElseThrow(() -> new CodeLightException(
                                                  ExceptionCodeType.COMMENT_NOT_FOUND));

            if (!parent.getPost().getId().equals(postId)) {
                throw new CodeLightException(ExceptionCodeType.INVALID_PARAMETER);
            }

            builder.parent(parent);
        }

        Comment comment = builder.build();
        Comment savedComment = commentRepository.save(comment);

        post.increaseComments();
        postRepository.save(post);

        return savedComment;
    }

    @Transactional
    public void updateComment(Long commentId, String newContent, User user) {
        Comment comment = commentRepository.findByIdAndUserAndDeletedFalse(commentId, user)
                                           .orElseThrow(() -> new CodeLightException(
                                               ExceptionCodeType.COMMENT_NOT_FOUND));
        comment.updateContent(newContent);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findByIdAndUserAndDeletedFalse(commentId, user)
                                           .orElseThrow(() -> new CodeLightException(
                                               ExceptionCodeType.COMMENT_NOT_FOUND));

        Post post = comment.getPost();

        comment.softDelete();
        commentRepository.save(comment);
    }
}
