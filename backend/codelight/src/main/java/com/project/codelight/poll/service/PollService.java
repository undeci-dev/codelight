package com.project.codelight.poll.service;

import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.poll.domain.Poll;
import com.project.codelight.poll.domain.PollOption;
import com.project.codelight.poll.domain.PollVote;
import com.project.codelight.poll.dto.request.PollCreateRequest;
import com.project.codelight.poll.repository.PollOptionRepository;
import com.project.codelight.poll.repository.PollRepository;
import com.project.codelight.poll.repository.PollVoteRepository;
import com.project.codelight.post.domain.Post;
import com.project.codelight.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;

    @Transactional
    public Poll createPoll(Post post, PollCreateRequest request) {
        validateOptions(request.options());

        Poll poll = Poll.builder()
                        .post(post)
                        .question(request.question())
                        .multipleChoice(request.multipleChoice())
                        .endsAt(request.endsAt())
                        .totalVotes(0)
                        .build();

        Poll savedPoll = pollRepository.save(poll);

        List<PollOption> pollOptions = new ArrayList<>();
        for (int i = 0; i < request.options().size(); i++) {
            PollOption option = PollOption.builder()
                                          .poll(savedPoll)
                                          .optionText(request.options().get(i).getOptionText())
                                          .votesCount(0)
                                          .displayOrder(i)
                                          .build();
            pollOptions.add(option);
        }
        pollOptionRepository.saveAll(pollOptions);

        return savedPoll;
    }

    @Transactional(readOnly = true)
    public Optional<Poll> getPollByPostId(Long postId) {
        return pollRepository.findByPostId(postId);
    }

    @Transactional(readOnly = true)
    public Poll getPollById(Long pollId) {
        return pollRepository.findByIdWithPost(pollId)
                             .orElseThrow(
                                 () -> new CodeLightException(ExceptionCodeType.POLL_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<PollOption> getPollOptions(Poll poll) {
        return pollOptionRepository.findByPollOrderByDisplayOrder(poll);
    }

    @Transactional(readOnly = true)
    public Set<Long> getVotedOptionIds(Poll poll, User user) {
        if (user == null) {
            return Set.of();
        }
        return new HashSet<>(pollVoteRepository.findVotedOptionIdsByPollAndUser(poll, user));
    }

    @Transactional(readOnly = true)
    public boolean hasUserVoted(Poll poll, User user) {
        if (user == null) {
            return false;
        }
        return pollVoteRepository.existsByPollAndUser(poll, user);
    }

    @Transactional
    public void vote(Long pollId, List<Long> optionIds, User user) {
        Poll poll = getPollById(pollId);

        validatePollNotExpired(poll);

        if (!poll.getMultipleChoice() && optionIds.size() > 1) {
            throw new CodeLightException(ExceptionCodeType.POLL_MULTIPLE_VOTE_NOT_ALLOWED);
        }

        boolean hasVoted = pollVoteRepository.existsByPollAndUser(poll, user);

        List<PollOption> options = pollOptionRepository.findAllById(optionIds);
        if (options.size() != optionIds.size()) {
            throw new CodeLightException(ExceptionCodeType.INVALID_PARAMETER);
        }

        for (PollOption option : options) {
            if (!option.getPoll().getId().equals(pollId)) {
                throw new CodeLightException(ExceptionCodeType.INVALID_PARAMETER);
            }

            boolean alreadyVotedThisOption = pollVoteRepository.existsByPollAndOptionAndUser(poll,
                option, user);
            if (alreadyVotedThisOption) {
                continue;
            }

            PollVote vote = PollVote.builder()
                                    .poll(poll)
                                    .option(option)
                                    .user(user)
                                    .build();
            pollVoteRepository.save(vote);

            option.incrementVotes();
            pollOptionRepository.save(option);
        }

        if (!hasVoted) {
            poll.incrementVotes();
            pollRepository.save(poll);
        }
    }

    @Transactional
    public void cancelVote(Long pollId, User user) {
        Poll poll = getPollById(pollId);

        List<PollVote> votes = pollVoteRepository.findByPollAndUser(poll, user);
        if (votes.isEmpty()) {
            throw new CodeLightException(ExceptionCodeType.POLL_VOTE_NOT_FOUND);
        }

        for (PollVote vote : votes) {
            PollOption option = vote.getOption();
            option.decrementVotes();
            pollOptionRepository.save(option);
            pollVoteRepository.delete(vote);
        }

        poll.decrementVotes();
        pollRepository.save(poll);
    }

    @Transactional
    public void deletePollByPost(Post post) {
        pollRepository.findByPost(post).ifPresent(poll -> {
            pollVoteRepository.deleteAllByPoll(poll);
            pollOptionRepository.deleteAllByPoll(poll);
            pollRepository.deleteByPost(post);
        });
    }

    @Transactional(readOnly = true)
    public boolean hasAnyVotes(Post post) {
        return pollRepository.findByPost(post)
            .map(poll -> pollVoteRepository.countByPoll(poll) > 0)
            .orElse(false);
    }

    @Transactional
    public void updatePoll(Post post, PollCreateRequest request) {
        Poll poll = pollRepository.findByPost(post)
            .orElseThrow(() -> new CodeLightException(ExceptionCodeType.POLL_NOT_FOUND));

        poll.update(request.question(), request.endsAt());
        pollRepository.save(poll);
    }

    private void validateOptions(List<PollOption> options) {
        if (options == null || options.size() < 2) {
            throw new CodeLightException(ExceptionCodeType.POLL_OPTIONS_REQUIRED);
        }
        if (options.size() > 4) {
            throw new CodeLightException(ExceptionCodeType.POLL_OPTIONS_EXCEEDED);
        }

        Set<String> uniqueOptions = options.stream()
                                           .map(PollOption::getOptionText)
                                           .collect(Collectors.toSet());

        if (uniqueOptions.size() != options.size()) {
            throw new CodeLightException(ExceptionCodeType.POLL_OPTION_DUPLICATED);
        }
    }

    private void validatePollNotExpired(Poll poll) {
        if (poll.getEndsAt() != null && poll.getEndsAt().isBefore(LocalDateTime.now())) {
            throw new CodeLightException(ExceptionCodeType.POLL_NOT_FOUND);
        }
    }
}
