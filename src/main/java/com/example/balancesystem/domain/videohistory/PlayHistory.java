package com.example.balancesystem.domain.videohistory;

import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Access(AccessType.FIELD)
@Table(name = "play_history", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "video_id"})})
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

    private boolean isCompleted = false;

    public PlayHistory(User user, Video video) {
        this.user = user;
        this.video = video;
    }

    public PlayHistory() {

    }

    public void setLastPlayedAt(int lastPlayedAt) {
        this.lastPlayedAt = lastPlayedAt;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
