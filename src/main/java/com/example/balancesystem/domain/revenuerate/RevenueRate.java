package com.example.balancesystem.domain.revenuerate;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "revenue_rate")
@Getter
public class RevenueRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_views", nullable = false)
    private Long minViews; // 조회수 최소 범위

    @Column(name = "max_views", nullable = false)
    private Long maxViews; // 조회수 최대 범위

    @Column(name = "rate", nullable = false)
    private double rate; // 해당 구간의 단가

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RevenueType type; // 영상 조회수인지 광고 조회수인지 구분

    public RevenueRate(Long minViews, Long maxViews, double rate, RevenueType type) {
        this.minViews = minViews;
        this.maxViews = maxViews;
        this.rate = rate;
        this.type = type;
    }

    protected RevenueRate() {
    }
}
