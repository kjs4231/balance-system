package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ad_history")
public class AdHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adHistoryId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)  // 새로운 필드 추가
    private Video video;

    private boolean viewed = false;

    @Column(nullable = false)
    private LocalDateTime viewDate;

    public AdHistory(User user, Ad ad, Video video, LocalDateTime viewDate) {
        this.user = user;
        this.ad = ad;
        this.video = video;
        this.viewDate = viewDate;
        this.viewed = true;
    }

    public void markAsViewed() {
        this.viewed = true;
    }
}
