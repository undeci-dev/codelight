package com.project.codelight.poll.dto.response;

import com.project.codelight.poll.domain.PollOption;

public record PollOptionResponse(
    Long optionId,
    String optionText,
    int votesCount,
    int displayOrder,
    boolean voted,
    double percentage
) {

    public static PollOptionResponse from(PollOption option, boolean voted, int totalVotes) {
        double percentage = totalVotes > 0
            ? Math.round((double) option.getVotesCount() / totalVotes * 1000) / 10.0
            : 0.0;

        return new PollOptionResponse(
            option.getId(),
            option.getOptionText(),
            option.getVotesCount(),
            option.getDisplayOrder(),
            voted,
            percentage
        );
    }
}
