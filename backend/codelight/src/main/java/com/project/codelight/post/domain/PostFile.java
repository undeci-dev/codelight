package com.project.codelight.post.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "post_files")
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class PostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl;

    @Column(name = "s3_key", length = 500)
    private String s3Key;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder
    private PostFile(Post post,
                     String fileUrl,
                     String s3Key,
                     String fileName,
                     Long fileSize,
                     Integer displayOrder) {
        this.post = post;
        this.fileUrl = fileUrl;
        this.s3Key = s3Key;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public void changeDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}