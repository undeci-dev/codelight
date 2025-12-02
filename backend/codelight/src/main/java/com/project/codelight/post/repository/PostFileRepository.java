package com.project.codelight.post.repository;

import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    void deleteAllByPost(Post post);
}
