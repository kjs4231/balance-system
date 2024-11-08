package com.example.contentservice.adhistory.dsl;

import com.example.contentservice.ad.Ad;
import com.example.contentservice.video.Video;

import java.time.LocalDate;

public interface CustomAdHistoryRepository {
    boolean existsByUserIdAndAdAndVideoAndViewDate(Long userId, Ad ad, Video video, LocalDate viewDate);
}
