package com.example.balancesystem.videohistory;

import com.example.balancesystem.user.User;
import com.example.balancesystem.video.Video;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
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

    private int lastPlayedAt = 0;

    private boolean isCompleted = false;

}