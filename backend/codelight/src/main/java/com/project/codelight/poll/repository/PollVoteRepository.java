package com.project.codelight.poll.repository;

import com.project.codelight.poll.domain.Poll;
import com.project.codelight.poll.domain.PollOption;
import com.project.codelight.poll.domain.PollVote;
import com.project.codelight.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PollVoteRepository extends JpaRepository<PollVote, Long> {

    boolean existsByPollAndUser(Poll poll, User user);

    boolean existsByPollAndOptionAndUser(Poll poll, PollOption option, User user);

    List<PollVote> findByPollAndUser(Poll poll, User user);

    Optional<PollVote> findByPollAndOptionAndUser(Poll poll, PollOption option, User user);

    @Query("SELECT pv.option.id FROM PollVote pv WHERE pv.poll = :poll AND pv.user = :user")
    List<Long> findVotedOptionIdsByPollAndUser(@Param("poll") Poll poll, @Param("user") User user);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM PollVote pv WHERE pv.poll = :poll")
    void deleteAllByPoll(@Param("poll") Poll poll);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM PollVote pv WHERE pv.option = :option")
    void deleteAllByOption(@Param("option") PollOption option);

    int countByPoll(Poll poll);
}
