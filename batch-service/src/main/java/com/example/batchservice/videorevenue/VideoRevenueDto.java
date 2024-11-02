package com.example.batchservice.videorevenue;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class VideoRevenueDto {
    private Long revenueId;
    private String videoTitle;
    private BigDecimal viewRevenue;
    private BigDecimal adRevenue;
    private BigDecimal totalRevenue;
    private LocalDate date;

    public VideoRevenueDto() {}

    public VideoRevenueDto(Long revenueId, String videoTitle, BigDecimal viewRevenue, BigDecimal adRevenue, BigDecimal totalRevenue, LocalDate date) {
        this.revenueId = revenueId;
        this.videoTitle = videoTitle;
        this.viewRevenue = viewRevenue;
        this.adRevenue = adRevenue;
        this.totalRevenue = totalRevenue;
        this.date = date;
    }

}
