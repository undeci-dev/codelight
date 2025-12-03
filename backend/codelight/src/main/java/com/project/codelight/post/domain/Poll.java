package com.project.codelight.post.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "polls")
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    @Column(name = "question", length = 500, nullable = false)
    private String question;

    @Column(name = "multiple_choice")
    private Boolean multipleChoice;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "total_votes")
    private Integer totalVotes;

    @OneToMany(mappedBy = "poll", fetch = FetchType.LAZY)
    private List<PollOption> options = new ArrayList<>();

    @Builder
    private Poll(Post post,
                 String question,
                 Boolean multipleChoice,
                 LocalDateTime endsAt,
                 Integer totalVotes) {
        this.post = post;
        this.question = question;
        this.multipleChoice = multipleChoice != null ? multipleChoice : false;
        this.endsAt = endsAt;
        this.totalVotes = totalVotes != null ? totalVotes : 0;
    }

    public void updateQuestion(String question) {
        this.question = question;
    }

    public void update(String question, LocalDateTime endsAt) {
        this.question = question;
        this.endsAt = endsAt;
    }

    public void incrementVotes() {
        if (this.totalVotes == null) {
            this.totalVotes = 1;
        } else {
            this.totalVotes++;
        }
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public void setMultipleChoice(Boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }
}