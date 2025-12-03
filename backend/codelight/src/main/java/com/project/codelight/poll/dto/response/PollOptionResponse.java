package com.project.codelight.poll.dto.response;

import com.project.codelight.poll.domain.PollOption;

public record PollOptionResponse(
    Long optionId,
    String optionText,
    int votesCount,
    int displayOrder,
    boolean voted
) {

    public static PollOptionResponse from(PollOption option, boolean voted) {
        return new PollOptionResponse(
            option.getId(),
            option.getOptionText(),
            option.getVotesCount(),
            option.getDisplayOrder(),
            voted
        );
    }
}
