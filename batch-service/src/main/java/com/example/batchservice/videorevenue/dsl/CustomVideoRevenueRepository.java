package com.example.batchservice.videorevenue.dsl;

import com.example.batchservice.videorevenue.VideoRevenue;

import java.time.LocalDate;
import java.util.List;

public interface CustomVideoRevenueRepository {
    List<VideoRevenue> findByDateBetween(LocalDate startDate, LocalDate endDate);
    boolean existsByVideoIdAndDate(Long videoId, LocalDate date);
}
