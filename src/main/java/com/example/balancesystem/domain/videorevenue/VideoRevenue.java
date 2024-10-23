package com.example.balancesystem.domain.videorevenue;

import com.example.balancesystem.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "video_revenue")
public class VideoRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoRevenueId;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private String date; // 정산 날짜
    private double uploadRevenue; // 업로드 조회수에 따른 정산 금액
    private double adRevenue; // 광고 조회수에 따른 정산 금액
    private double totalRevenue; // 총 정산 금액

    protected VideoRevenue() {
    }

    public VideoRevenue(Video video, String date, double uploadRevenue, double adRevenue, double totalRevenue) {
        this.video = video;
        this.date = date;
        this.uploadRevenue = uploadRevenue;
        this.adRevenue = adRevenue;
        this.totalRevenue = totalRevenue;
    }
}
