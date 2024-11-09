package com.example.balancesystem.global.videostats.dsl;

import java.time.LocalDate;
import java.util.List;

import com.example.balancesystem.global.videostats.VideoStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomVideoStatisticsRepository {

    List<VideoStatistics> findTop5ByDateBetweenOrderByViewCountDesc(LocalDate startDate, LocalDate endDate);

    List<VideoStatistics> findTop5ByDateBetweenOrderByTotalPlayTimeDesc(LocalDate startDate, LocalDate endDate);

    boolean existsByVideoIdAndDate(Long videoId, LocalDate date);

    Page<VideoStatistics> findByDate(LocalDate date, Pageable pageable);

    List<VideoStatistics> findByVideoIdAndDate(Long videoId, LocalDate date);

    Long getDailyViewCountByVideoId(Long videoId, LocalDate date);

    Long getDailyAdViewCountByVideoId(Long videoId, LocalDate date);
}
