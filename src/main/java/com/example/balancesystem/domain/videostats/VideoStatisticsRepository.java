package com.example.balancesystem.domain.videostats;

import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    } // 모든 비디오를 조회하는 메서드 (영상 목록을 가져오기 위한 메서드)
    @Query("SELECT v FROM Video v")
    List<Video> findAllVideos();

    // 주간 통계가 이미 존재하는지 확인하는 메서드
    boolean existsByVideoAndStatTypeAndDateBetween(Video video, StatType statType, LocalDate startDate, LocalDate endDate);

    // 일간 통계를 조회하는 메서드 정의 (예: 주간 통계의 특정 주에 대한 일간 통계)
    @Query("SELECT vs FROM VideoStatistics vs WHERE vs.video = :video AND vs.date BETWEEN :startDate AND :endDate")
    List<VideoStatistics> findDailyStatisticsForWeek(@Param("video") Video video, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



}