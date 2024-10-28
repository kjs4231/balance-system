package com.example.balancesystem.domain.videostats;

import com.example.balancesystem.domain.video.Video;
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

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long totalPlayTime;

    @Column(nullable = false)
    private Long adViewCount;

    public VideoStatistics(Video video, LocalDate date, Long viewCount, Long totalPlayTime, Long adViewCount) {
        this.video = video;
        this.date = date;
        this.viewCount = viewCount;
        this.totalPlayTime = totalPlayTime;
        this.adViewCount = adViewCount;
    }

    public VideoStatistics() {
    }
}
