package com.project.codelight.post.service;

import com.project.codelight.file.dto.FileUploadResponse;
import com.project.codelight.file.service.S3Service;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.link.dto.request.LinkCreateRequest;
import com.project.codelight.link.dto.response.LinkPreviewResponse;
import com.project.codelight.link.service.LinkPreviewService;
import com.project.codelight.poll.dto.request.PollCreateRequest;
import com.project.codelight.poll.service.PollService;
import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostFile;
import com.project.codelight.post.domain.PostLink;
import com.project.codelight.post.dto.request.FileOrderUpdate;
import com.project.codelight.post.dto.request.PostCreateRequest;
import com.project.codelight.post.dto.request.PostUpdateRequest;
import com.project.codelight.post.repository.PostFileRepository;
import com.project.codelight.post.repository.PostLinkRepository;
import com.project.codelight.post.repository.PostRepository;
import com.project.codelight.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final PostLinkRepository postLinkRepository;
    private final PollService pollService;
    private final LinkPreviewService linkPreviewService;
    private final S3Service s3Service;

    public List<Post> getActivePosts() {
        return postRepository.findActivePosts();
    }

    public Optional<Post> getActivePostById(Long postId) {
        return postRepository.findByIdAndDeletedFalse(postId);
    }

    @Transactional
    public Post createPost(User user, PostCreateRequest request, List<MultipartFile> files) {
        Post post = Post.builder()
                        .user(user)
                        .content(request.content())
                        .build();

        Post savedPost = postRepository.save(post);

        // 파일 업로드 및 저장
        if (files != null && !files.isEmpty()) {
            List<PostFile> postFiles = uploadAndCreatePostFiles(savedPost, files);
            postFileRepository.saveAll(postFiles);
        }

        if (request.poll() != null) {
            pollService.createPoll(savedPost, request.poll());
        }

        if (request.links() != null && !request.links().isEmpty()) {
            List<PostLink> postLinks = createPostLinks(savedPost, request.links());
            postLinkRepository.saveAll(postLinks);
        }

        return savedPost;
    }

    private List<PostFile> uploadAndCreatePostFiles(Post post, List<MultipartFile> files) {
        return uploadAndCreatePostFiles(post, files, 0);
    }

    private List<PostFile> uploadAndCreatePostFiles(Post post, List<MultipartFile> files,
                                                    int startDisplayOrder) {
        List<PostFile> postFiles = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            FileUploadResponse uploadResponse = s3Service.uploadFile(file);

            PostFile postFile = PostFile.builder()
                                        .post(post)
                                        .fileUrl(uploadResponse.fileUrl())
                                        .s3Key(uploadResponse.s3Key())
                                        .fileName(uploadResponse.fileName())
                                        .fileSize(uploadResponse.fileSize())
                                        .displayOrder(startDisplayOrder + i)
                                        .build();

            postFiles.add(postFile);
        }

        return postFiles;
    }

    private List<PostLink> createPostLinks(Post post, List<LinkCreateRequest> links) {
        List<PostLink> postLinks = new ArrayList<>();

        for (LinkCreateRequest link : links) {
            LinkPreviewResponse preview = linkPreviewService.fetchLinkPreview(link.url());

            PostLink postLink = PostLink.builder()
                                        .post(post)
                                        .url(preview.url())
                                        .title(preview.title())
                                        .description(preview.description())
                                        .imageUrl(preview.image())
                                        .domain(preview.domain())
                                        .build();

            postLinks.add(postLink);
        }

        return postLinks;
    }

    @Transactional
    public void softDeletePost(Long postId, User user) {
        Post post = postRepository.findByIdAndUserAndDeletedFalse(postId, user)
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.POST_NOT_FOUND));

        pollService.deletePollByPost(post);
        deletePostFilesWithS3(post);
        postLinkRepository.deleteAllByPost(post);
        postRepository.softDeletePostById(postId);
    }

    private void deletePostFilesWithS3(Post post) {
        List<PostFile> postFiles = postFileRepository.findAllByPost(post);

        List<String> s3Keys = postFiles.stream()
                                       .map(PostFile::getS3Key)
                                       .toList();

        s3Service.deleteFiles(s3Keys);
        postFileRepository.deleteAllByPost(post);
    }

    @Transactional
    public void restorePost(Long postId) {
        postRepository.restorePostById(postId);
    }

    @Transactional
    public void updatePost(Long postId, User user, PostUpdateRequest request,
                           List<MultipartFile> files) {
        Post post = postRepository.findByIdAndUserAndDeletedFalse(postId, user)
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.POST_NOT_FOUND));

        post.updateContent(request.content());

        if (request.deleteFileIds() != null && !request.deleteFileIds().isEmpty()) {
            deletePostFilesByIds(post, request.deleteFileIds());
        }

        if (request.fileOrders() != null && !request.fileOrders().isEmpty()) {
            updateFileOrders(post, request.fileOrders());
        }

        if (files != null && !files.isEmpty()) {
            int maxDisplayOrder = getMaxDisplayOrder(post);
            List<PostFile> postFiles = uploadAndCreatePostFiles(post, files, maxDisplayOrder + 1);
            postFileRepository.saveAll(postFiles);
        }

        // Poll 처리: 투표자가 있으면 업데이트, 없으면 삭제 후 새로 생성
        updateOrReplacePoll(post, request.poll());

        if (request.deleteLinkIds() != null && !request.deleteLinkIds().isEmpty()) {
            postLinkRepository.deleteAllByIdInAndPost(request.deleteLinkIds(), post);
        }

        if (request.links() != null && !request.links().isEmpty()) {
            List<PostLink> postLinks = createPostLinks(post, request.links());
            postLinkRepository.saveAll(postLinks);
        }
    }

    private void deletePostFilesByIds(Post post, List<Long> fileIds) {
        List<PostFile> filesToDelete = postFileRepository.findAllByIdInAndPost(fileIds, post);

        List<String> s3Keys = filesToDelete.stream()
                                           .map(PostFile::getS3Key)
                                           .toList();

        s3Service.deleteFiles(s3Keys);
        postFileRepository.deleteAllByIdInAndPost(fileIds, post);
    }

    private int getMaxDisplayOrder(Post post) {
        List<PostFile> existingFiles = postFileRepository.findAllByPost(post);
        return existingFiles.stream()
                            .mapToInt(PostFile::getDisplayOrder)
                            .max()
                            .orElse(-1);
    }

    private void updateFileOrders(Post post, List<FileOrderUpdate> fileOrders) {
        List<Long> fileIds = fileOrders.stream()
                                       .map(FileOrderUpdate::fileId)
                                       .toList();

        List<PostFile> files = postFileRepository.findAllByIdInAndPost(fileIds, post);

        Map<Long, PostFile> fileMap = files.stream()
                                           .collect(Collectors.toMap(PostFile::getId,
                                               Function.identity()));

        for (FileOrderUpdate orderUpdate : fileOrders) {
            PostFile file = fileMap.get(orderUpdate.fileId());
            if (file != null) {
                file.changeDisplayOrder(orderUpdate.displayOrder());
            }
        }

        postFileRepository.saveAll(files);
    }

    private void updateOrReplacePoll(Post post, PollCreateRequest pollRequest) {
        boolean hasExistingPoll = post.getPoll() != null;
        boolean hasVotes = pollService.hasAnyVotes(post);

        if (pollRequest == null) {
            // 요청에 poll이 없으면 기존 poll 삭제 (투표자가 없을 때만)
            if (hasExistingPoll && !hasVotes) {
                pollService.deletePollByPost(post);
            }
            return;
        }

        if (hasExistingPoll && hasVotes) {
            // 기존 poll에 투표자가 있으면 question, endsAt만 업데이트
            pollService.updatePoll(post, pollRequest);
        } else {
            // 투표자가 없으면 기존 poll 삭제 후 새로 생성
            pollService.deletePollByPost(post);
            pollService.createPoll(post, pollRequest);
        }
    }
}
