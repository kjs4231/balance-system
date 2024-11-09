package com.example.balancesystem.global.videorevenue.dsl;

import com.example.balancesystem.global.videorevenue.VideoRevenue;

import java.time.LocalDate;
import java.util.List;

public interface CustomVideoRevenueRepository {
    List<VideoRevenue> findByDateBetween(LocalDate startDate, LocalDate endDate);
    boolean existsByVideoIdAndDate(Long videoId, LocalDate date);
}
