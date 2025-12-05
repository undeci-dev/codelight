package com.project.codelight.post.repository;

import com.project.codelight.post.domain.Post;
import com.project.codelight.post.domain.PostLink;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLinkRepository extends JpaRepository<PostLink, Long> {

    void deleteAllByPost(Post post);

    void deleteAllByIdInAndPost(List<Long> ids, Post post);
}
