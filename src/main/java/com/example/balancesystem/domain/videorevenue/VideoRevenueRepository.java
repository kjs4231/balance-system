package com.example.balancesystem.domain.videorevenue;

import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface VideoRevenueRepository extends JpaRepository<VideoRevenue, Long> {
//    List<VideoRevenue> findByVideoAndDate(Video video, Date date);
    List<VideoRevenue> findAllByVideoAndDateBetween(Video video, LocalDate startDate, LocalDate endDate);
    List<VideoRevenue> findByDateBetween(LocalDate startDate, LocalDate endDate);
}