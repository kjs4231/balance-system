package com.example.balancesystem.global.revenuerate;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "revenue_rate")
@Getter
public class RevenueRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revenueRateId;

    @Column(name = "min_views", nullable = true)
    private Long minViews;

    @Column(name = "max_views", nullable = true)
    private Long maxViews;

    @Column(name = "rate", nullable = false)
    private double rate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RevenueType type;

    public RevenueRate(Long minViews, Long maxViews, double rate, RevenueType type) {
        this.minViews = minViews;
        this.maxViews = maxViews;
        this.rate = rate;
        this.type = type;
    }

    protected RevenueRate() {
    }
}
