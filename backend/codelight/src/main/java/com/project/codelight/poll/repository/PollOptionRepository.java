package com.project.codelight.poll.repository;

import com.project.codelight.post.domain.Poll;
import com.project.codelight.post.domain.PollOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

    @Query("SELECT po FROM PollOption po WHERE po.poll = :poll ORDER BY po.displayOrder ASC")
    List<PollOption> findByPollOrderByDisplayOrder(@Param("poll") Poll poll);

    List<PollOption> findByPollId(Long pollId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM PollOption po WHERE po.poll = :poll")
    void deleteAllByPoll(@Param("poll") Poll poll);
}
