package com.project.codelight.comment.repository;

import com.project.codelight.comment.domain.Comment;
import com.project.codelight.post.domain.Post;
import com.project.codelight.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.user " +
           "WHERE c.post = :post AND c.parent IS NULL AND c.deleted = false " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findRootCommentsByPost(@Param("post") Post post);

    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.user " +
           "WHERE c.parent = :parent AND c.deleted = false " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParent(@Param("parent") Comment parent);

    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.user " +
           "LEFT JOIN FETCH c.children " +
           "WHERE c.post = :post AND c.parent IS NULL AND c.deleted = false " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findRootCommentsWithRepliesByPost(@Param("post") Post post);

    Optional<Comment> findByIdAndDeletedFalse(Long id);

    Optional<Comment> findByIdAndUserAndDeletedFalse(Long id, User user);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post AND c.deleted = false")
    int countByPost(@Param("post") Post post);
}
