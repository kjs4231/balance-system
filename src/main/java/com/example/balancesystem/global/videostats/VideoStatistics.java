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
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long totalPlayTime = 0L;

    @Column(nullable = false)
    private Long adViewCount = 0L;

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
