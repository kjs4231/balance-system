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
    private LocalDateTime viewDate; // 시청 일자 추가

    @Column(nullable = false)
    private int playTime; // 재생 시간 (초) 추가

    // 생성자에서 기본값 설정
    public PlayHistory(User user, Video video, LocalDateTime viewDate, int playTime) {
        this.user = user;
        this.video = video;
        this.viewDate = viewDate;
        this.playTime = playTime;
        this.isCompleted = false;  // 기본값 설정
    }

    public PlayHistory() {}

    public void setLastPlayedAt(int lastPlayedAt) {
        this.lastPlayedAt = lastPlayedAt;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }
}