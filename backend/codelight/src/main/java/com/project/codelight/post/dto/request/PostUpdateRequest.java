package com.project.codelight.post.dto.request;

import com.project.codelight.link.dto.request.LinkCreateRequest;
import com.project.codelight.poll.dto.request.PollCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PostUpdateRequest(
    @NotBlank(message = "내용은 필수입니다.")
    String content,

    @Valid
    PollCreateRequest poll,

    @Valid
    List<LinkCreateRequest> links,

    List<Long> deleteFileIds,

    List<FileOrderUpdate> fileOrders,

    List<Long> deleteLinkIds
) {
}
