package com.project.codelight.poll.dto.response;

import com.project.codelight.poll.domain.Poll;
import com.project.codelight.poll.domain.PollOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public record PollResponse(
    Long pollId,
    String question,
    boolean multipleChoice,
    LocalDateTime endsAt,
    int totalVotes,
    boolean hasVoted,
    boolean isExpired,
    List<Long> votedOptionIds,
    List<PollOptionResponse> options
) {

    public static PollResponse from(Poll poll, List<PollOption> options, Set<Long> votedOptionIds,
                                    boolean hasVoted) {
        int totalVotes = poll.getTotalVotes() != null ? poll.getTotalVotes() : 0;

        List<PollOptionResponse> optionResponses = options.stream()
            .sorted(Comparator.comparing(PollOption::getDisplayOrder))
            .map(option -> PollOptionResponse.from(
                option, votedOptionIds.contains(option.getId()), totalVotes))
            .toList();

        boolean isExpired = poll.getEndsAt() != null && poll.getEndsAt().isBefore(LocalDateTime.now());

        return new PollResponse(
            poll.getId(),
            poll.getQuestion(),
            poll.getMultipleChoice() != null && poll.getMultipleChoice(),
            poll.getEndsAt(),
            totalVotes,
            hasVoted,
            isExpired,
            votedOptionIds.stream().toList(),
            optionResponses
        );
    }

    public static PollResponse from(Poll poll, List<PollOption> options) {
        int totalVotes = poll.getTotalVotes() != null ? poll.getTotalVotes() : 0;

        List<PollOptionResponse> optionResponses = options.stream()
            .sorted(Comparator.comparing(PollOption::getDisplayOrder))
            .map(option -> PollOptionResponse.from(option, false, totalVotes))
            .toList();

        boolean isExpired = poll.getEndsAt() != null && poll.getEndsAt().isBefore(LocalDateTime.now());

        return new PollResponse(
            poll.getId(),
            poll.getQuestion(),
            poll.getMultipleChoice() != null && poll.getMultipleChoice(),
            poll.getEndsAt(),
            totalVotes,
            false,
            isExpired,
            List.of(),
            optionResponses
        );
    }
}
