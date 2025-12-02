package com.project.codelight.post.repository;

import com.project.codelight.post.domain.Post;
import com.project.codelight.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"files, links"})
    @Query("SELECT p FROM Post p WHERE p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findActivePosts();

    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findAllByUserNotDeleted(@Param("user") User user);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Post p SET p.deleted = false WHERE p.id = :postId")
    void restorePostById(@Param("postId") Long postId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Post p SET p.deleted = true WHERE p.id = :postId")
    void softDeletePostById(@Param("postId") Long postId);

    Optional<Post> findByIdAndUserAndDeletedFalse(Long id, User user);

    @EntityGraph(attributePaths = {"files, links"})
    @Query("SELECT p FROM Post p WHERE p.id = :postId AND p.deleted = false ORDER BY p.createdAt DESC")
    Optional<Post> findByIdAndDeletedFalse(Long postId);
}