package com.project.codelight.post.domain;

import static lombok.AccessLevel.PROTECTED;

import com.project.codelight.BaseEntity;
import com.project.codelight.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "likes_count")
    private int likesCount;

    @Builder
    private Comment(Post post,
                    User user,
                    Comment parent,
                    String content,
                    Integer likesCount) {
        this.post = post;
        this.user = user;
        this.parent = parent;
        this.content = content;
        this.likesCount = likesCount != null ? likesCount : 0;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void increaseLikes() {
        this.likesCount++;
    }

    public void decreaseLikes() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

    public void addChild(Comment child) {
        children.add(child);
        child.parent = this;
    }

    public void removeChild(Comment child) {
        children.remove(child);
        child.parent = null;
    }
}