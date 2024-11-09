package com.example.balancesystem.global.videostats;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "video_statistics", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"video_id", "date"})
})
@Getter
public class VideoStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticId;

    private Long videoId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long totalPlayTime;

    @Column(nullable = false)
    private Long adViewCount;

    public VideoStatistics(Long videoId, LocalDate date, Long viewCount, Long totalPlayTime, Long adViewCount) {
        this.videoId = videoId;
        this.date = date;
        this.viewCount = viewCount;
        this.totalPlayTime = totalPlayTime;
        this.adViewCount = adViewCount;
    }

    public VideoStatistics() {
    }
}
