package com.project.codelight.poll.controller;

import com.project.codelight.auth.security.model.CustomUserDetails;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.poll.domain.Poll;
import com.project.codelight.poll.domain.PollOption;
import com.project.codelight.poll.dto.request.PollVoteRequest;
import com.project.codelight.poll.dto.response.PollResponse;
import com.project.codelight.poll.service.PollService;
import com.project.codelight.user.domain.User;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PollController {

    private final PollService pollService;

    @GetMapping("/api/post/{postId}/poll")
    public ResponseEntity<PollResponse> getPollByPost(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        Poll poll = pollService.getPollByPostId(postId)
                               .orElseThrow(
                                   () -> new CodeLightException(ExceptionCodeType.POLL_NOT_FOUND));

        User user = userDetails != null ? userDetails.getUser() : null;

        return ResponseEntity.ok(buildPollResponse(poll, user));
    }

    @GetMapping("/api/poll/{pollId}")
    public ResponseEntity<PollResponse> getPoll(
        @PathVariable Long pollId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        Poll poll = pollService.getPollById(pollId);
        User user = userDetails != null ? userDetails.getUser() : null;

        return ResponseEntity.ok(buildPollResponse(poll, user));
    }

    @PostMapping("/api/poll/{pollId}/vote")
    public ResponseEntity<PollResponse> vote(
        @PathVariable Long pollId,
        @Valid @RequestBody PollVoteRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        pollService.vote(pollId, request.optionIds(), user);

        Poll poll = pollService.getPollById(pollId);
        return ResponseEntity.ok(buildPollResponse(poll, user));
    }

    @DeleteMapping("/api/poll/{pollId}/vote")
    public ResponseEntity<Void> cancelVote(
        @PathVariable Long pollId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        pollService.cancelVote(pollId, user);

        return ResponseEntity.noContent().build();
    }

    private PollResponse buildPollResponse(Poll poll, User user) {
        List<PollOption> options = pollService.getPollOptions(poll);
        Set<Long> votedOptionIds = pollService.getVotedOptionIds(poll, user);
        boolean hasVoted = pollService.hasUserVoted(poll, user);

        return PollResponse.from(poll, options, votedOptionIds, hasVoted);
    }
}
