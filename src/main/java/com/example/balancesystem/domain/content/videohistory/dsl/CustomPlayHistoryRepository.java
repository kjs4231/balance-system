package com.example.balancesystem.domain.content.videohistory.dsl;

import com.example.balancesystem.domain.content.video.Video;
import com.example.balancesystem.domain.content.videohistory.PlayHistory;

import java.time.LocalDate;
import java.util.Optional;

public interface CustomPlayHistoryRepository {
    Optional<PlayHistory> findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(Long userId, Video video);
    long findPlayTimeByVideoIdAndDate(Long videoId, LocalDate date);
    long countByVideoIdAndDate(Long videoId, LocalDate date);
    long countAdViewsByVideoIdAndDate(Long videoId, LocalDate date);
}
