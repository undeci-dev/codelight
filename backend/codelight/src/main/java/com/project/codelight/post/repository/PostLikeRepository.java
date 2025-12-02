package com.project.codelight.post.repository;

import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostLike;
import com.project.codelight.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user = :user AND pl.post.id IN :postIds")
    List<Long> findLikedPostIdsByUserAndPostIds(User user, List<Long> postIds);
}
