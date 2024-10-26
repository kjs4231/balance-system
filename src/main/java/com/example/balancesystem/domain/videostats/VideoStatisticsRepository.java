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

    // 특정 월간에 해당하는 주간 통계를 조회하는 메서드 (비디오와 월간의 주간 데이터)
    default List<VideoStatistics> findWeeklyStatisticsForMonth(Video video, LocalDate startOfMonth, LocalDate endOfMonth) {
        return findByVideoAndStatTypeAndDateBetween(video, StatType.WEEK, startOfMonth, endOfMonth);
    }

    boolean existsByVideoAndStatTypeAndDate(Video video, StatType day, LocalDate date);

    List<VideoStatistics> findTop5ByStatTypeAndDateOrderByViewCountDesc(StatType statType, LocalDate date);
    List<VideoStatistics> findTop5ByStatTypeAndDateOrderByTotalPlayTimeDesc(StatType statType, LocalDate date);


}
