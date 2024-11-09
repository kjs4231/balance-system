package com.example.balancesystem.domain.content.adhistory.dsl;

import com.example.balancesystem.domain.content.ad.Ad;
import com.example.balancesystem.domain.content.video.Video;

import java.time.LocalDate;

public interface CustomAdHistoryRepository {
    boolean existsByUserIdAndAdAndVideoAndViewDate(Long userId, Ad ad, Video video, LocalDate viewDate);
}
