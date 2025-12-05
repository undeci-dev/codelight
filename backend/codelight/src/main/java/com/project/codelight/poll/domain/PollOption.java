package com.project.codelight.poll.domain;

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

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "poll_options")
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(name = "option_text", length = 255, nullable = false)
    private String optionText;

    @Column(name = "votes_count", nullable = false)
    private int votesCount;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder
    private PollOption(Poll poll,
                       String optionText,
                       Integer votesCount,
                       Integer displayOrder) {
        this.poll = poll;
        this.optionText = optionText;
        this.votesCount = votesCount != null ? votesCount : 0;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public void incrementVotes() {
        this.votesCount++;
    }

    public void decrementVotes() {
        if (this.votesCount > 0) {
            this.votesCount--;
        }
    }

    public void updateOptionText(String optionText) {
        this.optionText = optionText;
    }

    public void changeDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}