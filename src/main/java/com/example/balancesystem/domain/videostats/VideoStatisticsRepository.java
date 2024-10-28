package com.example.balancesystem.domain.videostats;

import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VideoStatisticsRepository extends JpaRepository<VideoStatistics, Long> {

    // 특정 비디오와 날짜 범위에 해당하는 통계를 가져오는 메서드 (일간 또는 주간)
    List<VideoStatistics> findByVideoAndStatTypeAndDateBetween(Video video, StatType statType, LocalDate startDate, LocalDate endDate);

    List<VideoStatistics> findTop5ByDateBetweenOrderByViewCountDesc(LocalDate startDate, LocalDate endDate);

    List<VideoStatistics> findTop5ByDateBetweenOrderByTotalPlayTimeDesc(LocalDate startDate, LocalDate endDate);

    boolean existsByVideoAndStatTypeAndDate(Video video, StatType day, LocalDate date);

}
