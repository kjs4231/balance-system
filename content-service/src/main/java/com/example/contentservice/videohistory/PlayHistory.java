package com.example.contentservice.videohistory;

import com.example.contentservice.video.Video;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Access(AccessType.FIELD)
@Table(name = "play_history")
public class PlayHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(nullable = false)
    private int lastPlayedAt = 0;

    @Column(name = "completed", nullable = false)
    private boolean isCompleted = false;

    @Column(nullable = false)
    private LocalDateTime viewDate;

    @Column(nullable = false)
    private int playTime;

    public PlayHistory(Long userId, Video video, LocalDateTime viewDate, int playTime) {
        this.userId = userId;
        this.video = video;
        this.viewDate = viewDate;
        this.playTime = playTime;
        this.isCompleted = false;
    }

    public PlayHistory() {}

    public void setLastPlayedAt(int lastPlayedAt) {
        this.lastPlayedAt = lastPlayedAt;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
}
