package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AdHistoryRepository extends JpaRepository<AdHistory, Long> {
    boolean existsByUserAndAdAndVideoAndViewDate(User user, Ad ad, Video video, LocalDate viewDate);
}
