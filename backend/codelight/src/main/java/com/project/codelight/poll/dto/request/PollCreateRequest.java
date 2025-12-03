package com.project.codelight.poll.dto.request;

import com.project.codelight.post.domain.PollOption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record PollCreateRequest(
    @NotBlank(message = "투표 질문은 필수입니다.")
    @Size(max = 500, message = "투표 질문은 최대 500자까지 입력 가능합니다.")
    String question,

    @NotEmpty(message = "투표 옵션은 최소 2개 이상이어야 합니다.")
    @Size(min = 2, max = 4, message = "투표 옵션은 2개 이상 4개 이하로 입력해야 합니다.")
    List<PollOption> options,

    Boolean multipleChoice,

    LocalDateTime endsAt
) {
}
