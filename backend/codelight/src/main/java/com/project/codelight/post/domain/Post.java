package com.project.codelight.post.domain;

import static lombok.AccessLevel.PROTECTED;

import com.project.codelight.BaseEntity;
import com.project.codelight.user.domain.User;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "posts")
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Post extends BaseEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "likes_count", nullable = false)
    private int likesCount;

    @Column(name = "comments_count", nullable = false)
    private int commentsCount;

    @Column(name = "shares_count", nullable = false)
    private int sharesCount;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<PostFile> files = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<PostLink> links = new HashSet<>();

    @Builder
    private Post(User user,
                 String content,
                 Integer likesCount,
                 Integer commentsCount,
                 Integer sharesCount,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt,
                 Boolean deleted,
                 Set<PostFile> files,
                 Set<PostLink> links
    ) {

        this.user = user;
        this.content = content;
        this.likesCount = likesCount != null ? likesCount : 0;
        this.commentsCount = commentsCount != null ? commentsCount : 0;
        this.sharesCount = sharesCount != null ? sharesCount : 0;
        this.files = files != null ? files : new HashSet<>();
        this.links = links != null ? links : new HashSet<>();

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

    public void increaseComments() {
        this.commentsCount++;
    }

    public void increaseShares() {
        this.sharesCount++;
    }
}
