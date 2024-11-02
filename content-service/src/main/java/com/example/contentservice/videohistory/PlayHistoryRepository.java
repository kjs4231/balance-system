package com.example.contentservice.videohistory;

import com.example.contentservice.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {

    // 특정 사용자와 비디오의 가장 최근 미완료된 재생 기록을 조회
    Optional<PlayHistory> findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(Long userId, Video video);

    @Query("SELECT COALESCE(SUM(ph.playTime), 0) FROM PlayHistory ph WHERE ph.video.videoId = :videoId AND DATE(ph.viewDate) = :date")
    long findPlayTimeByVideoIdAndDate(@Param("videoId") Long videoId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(ph) FROM PlayHistory ph WHERE ph.video.videoId = :videoId AND DATE(ph.viewDate) = :date")
    long countByVideoIdAndDate(@Param("videoId") Long videoId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(ah) FROM AdHistory ah WHERE ah.video.videoId = :videoId AND DATE(ah.viewDate) = :date")
    long countAdViewsByVideoIdAndDate(@Param("videoId") Long videoId, @Param("date") LocalDate date);
}