package com.example.batchservice.videostats;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VideoStatisticsRepository extends JpaRepository<VideoStatistics, Long> {

    List<VideoStatistics> findTop5ByDateBetweenOrderByViewCountDesc(LocalDate startDate, LocalDate endDate);

    List<VideoStatistics> findTop5ByDateBetweenOrderByTotalPlayTimeDesc(LocalDate startDate, LocalDate endDate);

    boolean existsByVideoIdAndDate(Long videoId, LocalDate date); // videoId 기반으로 조회

    Page<VideoStatistics> findByDate(LocalDate date, Pageable pageable);

    // 특정 날짜에 videoId 기준으로 dailyViewCount와 dailyAdViewCount 조회 메서드 추가
    List<VideoStatistics> findByVideoIdAndDate(Long videoId, LocalDate date);

    @Query("SELECT SUM(vs.viewCount) FROM VideoStatistics vs WHERE vs.videoId = :videoId AND vs.date = :date")
    Long getDailyViewCountByVideoId(@Param("videoId") Long videoId, @Param("date") LocalDate date);

    @Query("SELECT SUM(vs.adViewCount) FROM VideoStatistics vs WHERE vs.videoId = :videoId AND vs.date = :date")
    Long getDailyAdViewCountByVideoId(@Param("videoId") Long videoId, @Param("date") LocalDate date);
}
