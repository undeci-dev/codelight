package com.project.codelight.post.service;

import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.poll.dto.request.PollCreateRequest;
import com.project.codelight.poll.service.PollService;
import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostFile;
import com.project.codelight.post.dto.request.PostCreateRequest.FileInfo;
import com.project.codelight.post.repository.PostFileRepository;
import com.project.codelight.post.repository.PostRepository;
import com.project.codelight.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final PollService pollService;

    public List<Post> getActivePosts() {
        return postRepository.findActivePosts();
    }

    public Optional<Post> getActivePostById(Long postId) {
        return postRepository.findByIdAndDeletedFalse(postId);
    }

    @Transactional
    public Post createPostWithFiles(Post post, List<FileInfo> files) {
        Post savedPost = postRepository.save(post);

        if (files != null && !files.isEmpty()) {
            List<PostFile> postFiles = createPostFiles(savedPost, files);
            postFileRepository.saveAll(postFiles);
        }

        return savedPost;
    }

    @Transactional
    public Post createPostWithFilesAndPoll(Post post, List<FileInfo> files,
                                           PollCreateRequest pollRequest) {
        Post savedPost = postRepository.save(post);

        if (files != null && !files.isEmpty()) {
            List<PostFile> postFiles = createPostFiles(savedPost, files);
            postFileRepository.saveAll(postFiles);
        }

        if (pollRequest != null) {
            pollService.createPoll(savedPost, pollRequest);
        }

        return savedPost;
    }

    private List<PostFile> createPostFiles(Post post, List<FileInfo> files) {
        List<PostFile> postFiles = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            FileInfo file = files.get(i);

            PostFile postFile = PostFile.builder()
                                        .post(post)
                                        .fileUrl(file.fileUrl())
                                        .fileName(file.fileName())
                                        .fileSize(file.fileSize())
                                        .displayOrder(i)
                                        .build();

            postFiles.add(postFile);
        }

        return postFiles;
    }

    @Transactional
    public void softDeletePost(Long postId, User user) {
        Post post = postRepository.findByIdAndUserAndDeletedFalse(postId, user)
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.POST_NOT_FOUND));

        pollService.deletePollByPost(post);
        postFileRepository.deleteAllByPost(post);
        postRepository.softDeletePostById(postId);
    }

    @Transactional
    public void restorePost(Long postId) {
        postRepository.restorePostById(postId);
    }

    @Transactional
    public void updatePostContent(Long postId, String newContent, List<FileInfo> files,
                                  PollCreateRequest pollRequest, User user) {
        Post post = postRepository.findByIdAndUserAndDeletedFalse(postId, user)
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.POST_NOT_FOUND));
        post.updateContent(newContent);

        // 기존 파일 삭제 후 새 파일 저장
        postFileRepository.deleteAllByPost(post);
        if (files != null && !files.isEmpty()) {
            List<PostFile> postFiles = createPostFiles(post, files);
            postFileRepository.saveAll(postFiles);
        }

        // 기존 poll 삭제 후 새 poll 저장
        pollService.deletePollByPost(post);
        if (pollRequest != null) {
            pollService.createPoll(post, pollRequest);
        }
    }
}
