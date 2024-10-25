package com.example.balancesystem.domain.videohistory;

import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    public PlayHistory(User user, Video video, LocalDateTime viewDate, int playTime) {
        this.user = user;
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