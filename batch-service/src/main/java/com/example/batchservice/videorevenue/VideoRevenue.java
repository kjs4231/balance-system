package com.example.batchservice.videorevenue;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "video_revenue")
public class VideoRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revenueId;

    private Long videoId; // Video 객체 대신 videoId만 저장

    private LocalDate date;
    private BigDecimal viewRevenue;
    private BigDecimal adRevenue;
    private BigDecimal totalRevenue;

    protected VideoRevenue() {
    }

    public VideoRevenue(Long videoId, LocalDate date, BigDecimal viewRevenue, BigDecimal adRevenue, BigDecimal totalRevenue) {
        this.videoId = videoId;
        this.date = date;
        this.viewRevenue = viewRevenue;
        this.adRevenue = adRevenue;
        this.totalRevenue = totalRevenue;
    }
}
