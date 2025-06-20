package com.java_final_project.vocab_typing.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "personal_config")
public class PersonalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Long configId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "daily_word_limit")
    private int dailyWordLimit;

    @Column(name = "review_threshold")
    private int reviewThreshold;

    @Column(name = "start_speed_sec")
    private int startSpeedSec;

    @Column(name = "end_speed_sec")
    private int endSpeedSec;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // Getter / Setter
    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDailyWordLimit() {
        return dailyWordLimit;
    }

    public void setDailyWordLimit(int dailyWordLimit) {
        this.dailyWordLimit = dailyWordLimit;
    }

    public int getReviewThreshold() {
        return reviewThreshold;
    }

    public void setReviewThreshold(int reviewThreshold) {
        this.reviewThreshold = reviewThreshold;
    }

    public int getStartSpeedSec() {
        return startSpeedSec;
    }

    public void setStartSpeedSec(int startSpeedSec) {
        this.startSpeedSec = startSpeedSec;
    }

    public int getEndSpeedSec() {
        return endSpeedSec;
    }

    public void setEndSpeedSec(int endSpeedSec) {
        this.endSpeedSec = endSpeedSec;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
