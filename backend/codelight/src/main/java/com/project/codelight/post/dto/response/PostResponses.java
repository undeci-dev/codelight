package com.project.codelight.post.dto.response;

import java.util.List;

public record PostResponses(
    List<PostSummaryResponse> posts
) {

}
