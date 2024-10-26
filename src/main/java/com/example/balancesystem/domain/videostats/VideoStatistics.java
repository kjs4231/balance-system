
package com.example.balancesystem.domain.videostats;

import com.example.balancesystem.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "video_statistics", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"video_id", "statType", "date"})
})
@Getter
public class VideoStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticId;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatType statType; // "DAY", "WEEK", "MONTH"

    @Column(nullable = false)
    private LocalDate date; // 통계 기준 날짜

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long totalPlayTime;

    public VideoStatistics(Video video, StatType statType, LocalDate date, Long viewCount, Long totalPlayTime) {
        this.video = video;
        this.statType = statType;
        this.date = date;
        this.viewCount = viewCount;
        this.totalPlayTime = totalPlayTime;
    }

    public VideoStatistics() {
    }
}
