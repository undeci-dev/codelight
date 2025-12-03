package com.project.codelight.comment.repository;

import com.project.codelight.comment.domain.Comment;
import com.project.codelight.comment.domain.CommentLike;
import com.project.codelight.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

    boolean existsByCommentAndUser(Comment comment, User user);

    @Query("SELECT cl.comment.id FROM CommentLike cl " +
           "WHERE cl.user = :user AND cl.comment.id IN :commentIds")
    List<Long> findLikedCommentIdsByUserAndCommentIds(
        @Param("user") User user,
        @Param("commentIds") List<Long> commentIds);
}
