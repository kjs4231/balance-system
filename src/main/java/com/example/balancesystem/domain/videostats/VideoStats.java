package com.example.balancesystem.domain.videostats;

import com.example.balancesystem.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "video_stats")
public class VideoStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoStatsId;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Enumerated(EnumType.STRING)
    private PeriodType periodType; // 일, 주, 월을 enum으로 처리

    private int viewCount; // 조회수
    private int playTime; // 총 재생 시간 (초 단위)

    private String date; // 통계 집계 날짜

    public VideoStats(Video video, PeriodType periodType, int viewCount, int playTime, String date) {
        this.video = video;
        this.periodType = periodType;
        this.viewCount = viewCount;
        this.playTime = playTime;
        this.date = date;
    }

    protected VideoStats() {}
}
