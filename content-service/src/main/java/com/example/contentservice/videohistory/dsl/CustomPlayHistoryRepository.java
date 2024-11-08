package com.example.contentservice.videohistory.dsl;

import com.example.contentservice.video.Video;
import com.example.contentservice.videohistory.PlayHistory;

import java.time.LocalDate;
import java.util.Optional;

public interface CustomPlayHistoryRepository {
    Optional<PlayHistory> findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(Long userId, Video video);
    long findPlayTimeByVideoIdAndDate(Long videoId, LocalDate date);
    long countByVideoIdAndDate(Long videoId, LocalDate date);
    long countAdViewsByVideoIdAndDate(Long videoId, LocalDate date);
}
