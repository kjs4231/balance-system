package com.example.contentservice.adhistory;

import com.example.contentservice.ad.Ad;
import com.example.contentservice.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AdHistoryRepository extends JpaRepository<AdHistory, Long> {
    boolean existsByUserIdAndAdAndVideoAndViewDate(Long userId, Ad ad, Video video, LocalDate viewDate);
}
