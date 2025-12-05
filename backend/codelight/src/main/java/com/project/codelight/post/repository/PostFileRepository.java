package com.project.codelight.post.repository;

import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    List<PostFile> findAllByPost(Post post);

    List<PostFile> findAllByIdInAndPost(List<Long> ids, Post post);

    void deleteAllByPost(Post post);

    void deleteAllByIdInAndPost(List<Long> ids, Post post);
}
