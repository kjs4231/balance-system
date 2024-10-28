package com.example.balancesystem.domain.videohistory;

import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {

    // 특정 사용자와 비디오의 가장 최근 미완료된 재생 기록을 조회
    Optional<PlayHistory> findTopByUserAndVideoAndIsCompletedFalseOrderByViewDateDesc(User user, Video video);



}
