package com.project.codelight.post.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "post_links")
public class PostLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "url", length = 2000, nullable = false)
    private String url;

    @Column(name = "title", length = 500)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "domain", length = 255)
    private String domain;

    @Builder
    private PostLink(Post post,
                     String url,
                     String title,
                     String description,
                     String imageUrl,
                     String domain) {
        this.post = post;
        this.url = url;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.domain = domain;
    }

}