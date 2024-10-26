package com.example.balancesystem.domain.videorevenue;

import com.example.balancesystem.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
@Table(name = "video_revenue")
public class VideoRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revenueId;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private Date date; // 정산 날짜
    private double viewRevenue; // 업로드 조회수에 따른 정산 금액
    private double adRevenue; // 광고 조회수에 따른 정산 금액
    private double totalRevenue; // 총 정산 금액

    protected VideoRevenue() {
    }

    public VideoRevenue(Video video, Date date, double viewRevenue, double adRevenue, double totalRevenue) {
        this.video = video;
        this.date = date;
        this.viewRevenue = viewRevenue;
        this.adRevenue = adRevenue;
        this.totalRevenue = totalRevenue;
    }
}
