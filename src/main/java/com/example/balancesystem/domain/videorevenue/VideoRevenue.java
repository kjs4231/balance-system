package com.example.balancesystem.domain.videorevenue;

import com.example.balancesystem.domain.video.Video;
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

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private LocalDate date;
    private BigDecimal viewRevenue;
    private BigDecimal adRevenue;
    private BigDecimal totalRevenue;

    protected VideoRevenue() {
    }

    public VideoRevenue(Video video, LocalDate date, BigDecimal viewRevenue, BigDecimal adRevenue, BigDecimal totalRevenue) {
        this.video = video;
        this.date = date;
        this.viewRevenue = viewRevenue;
        this.adRevenue = adRevenue;
        this.totalRevenue = totalRevenue;
    }
}
