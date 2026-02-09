package fr.eletutour.chaosmonkeyapplication.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "watch_history")
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long videoId;

    private LocalDateTime watchedAt;

    private Integer progressPercentage; // 0-100

    private Boolean completed;

    public WatchHistory() {
        this.watchedAt = LocalDateTime.now();
        this.completed = false;
    }

    public WatchHistory(Long userId, Long videoId, Integer progressPercentage) {
        this.userId = userId;
        this.videoId = videoId;
        this.progressPercentage = progressPercentage;
        this.watchedAt = LocalDateTime.now();
        this.completed = progressPercentage >= 90;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public LocalDateTime getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(LocalDateTime watchedAt) {
        this.watchedAt = watchedAt;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
        this.completed = progressPercentage >= 90;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
