package com.example.balancesystem.global.batch;

import com.example.balancesystem.domain.content.videohistory.dsl.PlayHistoryRepository;
import com.example.balancesystem.global.videostats.VideoStatistics;
import com.example.balancesystem.global.videostats.dsl.VideoStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

public class DayStatisticsProcessor implements ItemProcessor<Long, VideoStatistics> {
    private static final Logger logger = LoggerFactory.getLogger(DayStatisticsProcessor.class);
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final PlayHistoryRepository playHistoryRepository;
    private final ConcurrentHashMap<Long, Long> cachedPlayTime;
    private final ConcurrentHashMap<Long, Long> cachedViewCount;

    public DayStatisticsProcessor(VideoStatisticsRepository videoStatisticsRepository,
                                  PlayHistoryRepository playHistoryRepository,
                                  ConcurrentHashMap<Long, Long> cachedPlayTime,
                                  ConcurrentHashMap<Long, Long> cachedViewCount) {
        this.videoStatisticsRepository = videoStatisticsRepository;
        this.playHistoryRepository = playHistoryRepository;
        this.cachedPlayTime = cachedPlayTime;
        this.cachedViewCount = cachedViewCount;
    }

    @Override
    public VideoStatistics process(Long videoId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        logger.info("Processing statistics for videoId: {}", videoId);
        if (!videoStatisticsRepository.existsByVideoIdAndDate(videoId, yesterday)) {
            long totalPlayTime = cachedPlayTime.computeIfAbsent(videoId,
                    id -> playHistoryRepository.findPlayTimeByVideoIdAndDate(id, yesterday));
            long viewCount = cachedViewCount.computeIfAbsent(videoId,
                    id -> playHistoryRepository.countByVideoIdAndDate(id, yesterday));
            long adViewCount = playHistoryRepository.countAdViewsByVideoIdAndDate(videoId, yesterday);
            return new VideoStatistics(videoId, yesterday, viewCount, totalPlayTime, adViewCount);
        } else {
            logger.info("Duplicate statistics data exists: video_id={}, date=", videoId, yesterday);
            return null;
        }
    }
}