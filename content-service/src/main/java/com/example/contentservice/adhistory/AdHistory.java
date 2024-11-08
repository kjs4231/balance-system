package com.example.contentservice.adhistory;

import com.example.contentservice.ad.Ad;
import com.example.contentservice.video.Video;
import com.example.global.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ad_history")
public class AdHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adHistoryId;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    private LocalDate viewDate;

    public AdHistory(Long userId, Ad ad, Video video, LocalDate viewDate) {
        this.userId = userId;
        this.ad = ad;
        this.video = video;
        this.viewDate = viewDate;
    }
}
