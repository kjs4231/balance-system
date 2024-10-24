package com.example.balancesystem.domain.videohistory;

import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {

    // 아직 완료되지 않은 특정 사용자와 비디오의 재생 기록을 조회
    Optional<PlayHistory> findByUserAndVideoAndIsCompletedFalse(User user, Video video);

    // 특정 사용자와 비디오의 가장 최근 미완료된 재생 기록을 조회
    Optional<PlayHistory> findTopByUserAndVideoAndIsCompletedFalseOrderByViewDateDesc(User user, Video video);

    // 특정 비디오에 대한 모든 재생 기록 조회
    List<PlayHistory> findByVideo(Video video);

    // 특정 날짜에 해당하는 비디오의 재생 기록 조회
    List<PlayHistory> findByVideoAndViewDateBetween(Video video, LocalDateTime startOfDay, LocalDateTime endOfDay);

    // 특정 비디오의 특정 날짜에 대한 총 재생 시간을 계산하는 메서드
    default Long calculateTotalPlayTimeForDay(Video video, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        List<PlayHistory> playHistories = findByVideoAndViewDateBetween(video, startOfDay, endOfDay);
        return playHistories.stream()
                .mapToLong(PlayHistory::getPlayTime)  // 각 재생 기록의 playTime 합산
                .sum();
    }
}
