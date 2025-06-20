package com.java_final_project.vocab_typing.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_logs")
public class ReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id") // 如果你沒有這個欄位，可移除
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "set_name", nullable = false)
    private String setName;

    @Column(name = "review_threshold")
    private Integer reviewThreshold;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt;

    @Column(name = "last_review_at")
    private LocalDateTime lastReviewAt;

    @Column(name = "status")
    private String status;

    // === Getter / Setter ===

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public Integer getReviewThreshold() {
        return reviewThreshold;
    }

    public void setReviewThreshold(Integer reviewThreshold) {
        this.reviewThreshold = reviewThreshold;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public LocalDateTime getNextReviewAt() {
        return nextReviewAt;
    }

    public void setNextReviewAt(LocalDateTime nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
    }

    public LocalDateTime getLastReviewAt() {
        return lastReviewAt;
    }

    public void setLastReviewAt(LocalDateTime lastReviewAt) {
        this.lastReviewAt = lastReviewAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
