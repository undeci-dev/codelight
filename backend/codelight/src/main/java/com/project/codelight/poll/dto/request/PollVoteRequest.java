package com.project.codelight.poll.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PollVoteRequest(
    @NotEmpty(message = "투표할 옵션을 선택해주세요.")
    List<Long> optionIds
) {
}
