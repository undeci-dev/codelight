package com.project.codelight.post.dto.response;

import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.poll.dto.response.PollResponse;
import com.project.codelight.post.domain.Poll;
import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostFile;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public record PostDetailResponse(
    Long postId,
    Long userId,
    String userName,
    String content,
    int likesCount,
    int commentsCount,
    int sharesCount,
    LocalDateTime createdAt,
    List<String> fileUrls,
    boolean isOwner,
    boolean liked,
    PollResponse poll
) {

    public static PostDetailResponse from(Post post, CustomUserDetails userDetails, boolean liked) {
        Set<PostFile> postFiles = post.getFiles();
        List<String> fileUrls = postFiles != null
            ? postFiles.stream()
                       .sorted(Comparator.comparing(PostFile::getDisplayOrder))
                       .map(PostFile::getFileUrl)
                       .toList()
            : List.of();

        boolean isOwner = userDetails != null
            && userDetails.getUser() != null
            && post.getUser() != null
            && post.getUser().getId().equals(userDetails.getUser().getId());

        Poll poll = post.getPoll();
        PollResponse pollResponse = poll != null
            ? PollResponse.from(poll, poll.getOptions())
            : null;

        return new PostDetailResponse(
            post.getId(),
            post.getUser().getId(),
            post.getUser().getName(),
            post.getContent(),
            post.getLikesCount(),
            post.getCommentsCount(),
            post.getSharesCount(),
            post.getCreatedAt(),
            fileUrls,
            isOwner,
            liked,
            pollResponse
        );
    }
}
