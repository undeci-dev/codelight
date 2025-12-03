package com.project.codelight.comment.dto.response;

import java.util.List;

public record CommentResponses(
    List<CommentResponse> comments,
    int totalCount
) {
}
