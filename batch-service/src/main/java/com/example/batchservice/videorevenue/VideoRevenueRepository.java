package com.example.batchservice.videorevenue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VideoRevenueRepository extends JpaRepository<VideoRevenue, Long> {
//    List<VideoRevenue> findAllByVideoAndDateBetween(Video video, LocalDate startDate, LocalDate endDate);
    List<VideoRevenue> findByDateBetween(LocalDate startDate, LocalDate endDate);


    boolean existsByVideoIdAndDate(Long videoId, LocalDate yesterday);
}