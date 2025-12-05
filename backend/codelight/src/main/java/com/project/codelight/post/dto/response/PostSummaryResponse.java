package com.project.codelight.post.dto.response;

import com.project.codelight.file.dto.FileResponse;
import com.project.codelight.link.dto.response.LinkPreviewResponse;
import com.project.codelight.poll.domain.Poll;
import com.project.codelight.poll.dto.response.PollResponse;
import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostFile;
import com.project.codelight.post.domain.PostLink;
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
    List<FileResponse> files,
    boolean liked,
    PollResponse poll,
    List<LinkPreviewResponse> links
) {

    public static PostSummaryResponse from(Post post, boolean liked,
                                           Set<Long> votedOptionIds, boolean hasVotedPoll,
                                           String cloudFrontDomain) {
        Set<PostFile> postFiles = post.getFiles();
        List<FileResponse> files = postFiles != null
            ? postFiles.stream()
                       .sorted(Comparator.comparing(PostFile::getDisplayOrder))
                       .map(postFile -> FileResponse.from(postFile, cloudFrontDomain))
                       .toList()
            : List.of();

        Poll poll = post.getPoll();
        PollResponse pollResponse = poll != null
            ? PollResponse.from(poll, poll.getOptions(), votedOptionIds, hasVotedPoll)
            : null;

        Set<PostLink> postLinks = post.getLinks();
        List<LinkPreviewResponse> linkResponses = postLinks != null
            ? postLinks.stream()
                       .map(link -> LinkPreviewResponse.of(
                           link.getId(),
                           link.getUrl(),
                           link.getTitle(),
                           link.getDescription(),
                           link.getImageUrl(),
                           link.getDomain()
                       ))
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
            files,
            liked,
            pollResponse,
            linkResponses
        );
    }
}
