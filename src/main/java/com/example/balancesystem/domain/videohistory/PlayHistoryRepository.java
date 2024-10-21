package com.example.balancesystem.domain.videohistory;

import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    Optional<PlayHistory> findByUserAndVideoAndIsCompletedFalse(User user, Video video);
    Optional<PlayHistory> findTopByUserAndVideoAndIsCompletedFalseOrderByViewDateDesc(User user, Video video);


}
