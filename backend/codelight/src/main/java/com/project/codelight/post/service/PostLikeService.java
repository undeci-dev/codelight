package com.project.codelight.post.service;

import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostLike;
import com.project.codelight.post.repository.PostLikeRepository;
import com.project.codelight.post.repository.PostRepository;
import com.project.codelight.user.domain.User;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public boolean toggleLike(Long postId, User user) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.POST_NOT_FOUND));

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.decreaseLikes();
            postRepository.save(post);
            return false;
        } else {
            PostLike newLike = PostLike.builder()
                                       .post(post)
                                       .user(user)
                                       .build();
            postLikeRepository.save(newLike);
            post.increaseLikes();
            postRepository.save(post);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postId, User user) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.POST_NOT_FOUND));
        return postLikeRepository.existsByPostAndUser(post, user);
    }

    @Transactional(readOnly = true)
    public Set<Long> getLikedPostIds(List<Long> postIds, User user) {
        if (user == null || postIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(postLikeRepository.findLikedPostIdsByUserAndPostIds(user, postIds));
    }
}
