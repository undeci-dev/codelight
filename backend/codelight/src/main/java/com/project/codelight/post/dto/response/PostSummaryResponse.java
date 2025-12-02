package com.project.codelight.post.dto.response;

import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostFile;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public record PostSummaryResponse(
    Long postId,
    String userName,
    String contentPreview,
    int likesCount,
    int commentsCount,
    int sharesCount,
    LocalDateTime createdAt,
    List<String> fileUrls,
    boolean liked
) {

    public static PostSummaryResponse from(Post post, boolean liked) {
        Set<PostFile> postFiles = post.getFiles();
        List<String> fileUrls = postFiles != null
            ? postFiles.stream()
                       .sorted(Comparator.comparing(PostFile::getDisplayOrder))
                       .map(PostFile::getFileUrl)
                       .toList()
            : List.of();

        return new PostSummaryResponse(
            post.getId(),
            post.getUser().getName(),
            post.getContent().length() > 100
                ? post.getContent().substring(0, 100) + "..."
                : post.getContent(),
            post.getLikesCount(),
            post.getCommentsCount(),
            post.getSharesCount(),
            post.getCreatedAt(),
            fileUrls,
            liked
        );
    }
}
