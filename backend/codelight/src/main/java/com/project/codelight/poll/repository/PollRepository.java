package com.project.codelight.poll.repository;

import com.project.codelight.post.domain.Poll;
import com.project.codelight.post.domain.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PollRepository extends JpaRepository<Poll, Long> {

    Optional<Poll> findByPost(Post post);

    Optional<Poll> findByPostId(Long postId);

    @Query("SELECT p FROM Poll p " +
           "LEFT JOIN FETCH p.post " +
           "WHERE p.id = :pollId")
    Optional<Poll> findByIdWithPost(@Param("pollId") Long pollId);

    boolean existsByPost(Post post);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Poll p WHERE p.post = :post")
    void deleteByPost(@Param("post") Post post);
}
