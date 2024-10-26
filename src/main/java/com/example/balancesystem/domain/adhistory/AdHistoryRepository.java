package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AdHistoryRepository extends JpaRepository<AdHistory, Long> {
    boolean existsByUserAndAdAndViewedTrue(User user, Ad ad);
    Optional<AdHistory> findByUserAndAd(User user, Ad ad);

    boolean existsByUserAndAdAndViewDateAndViewedTrue(User user, Ad ad, LocalDateTime viewDate);
    boolean existsByUserAndAdAndVideoAndViewedTrue(User user, Ad ad, Video video);
}
