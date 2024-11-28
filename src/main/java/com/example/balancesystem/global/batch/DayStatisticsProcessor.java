package com.example.balancesystem.global.batch;

import com.example.balancesystem.domain.content.playhistory.dsl.PlayHistoryRepository;
import com.example.balancesystem.global.videostats.VideoStatistics;
import com.example.balancesystem.global.videostats.dsl.VideoStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

public class DayStatisticsProcessor implements ItemProcessor<Long, VideoStatistics> {
    private static final Logger logger = LoggerFactory.getLogger(DayStatisticsProcessor.class);
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final PlayHistoryRepository playHistoryRepository;

    public DayStatisticsProcessor(VideoStatisticsRepository videoStatisticsRepository,
                                  PlayHistoryRepository playHistoryRepository) {
        this.videoStatisticsRepository = videoStatisticsRepository;
        this.playHistoryRepository = playHistoryRepository;
    }

    @Override
    public VideoStatistics process(Long videoId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        logger.info("Processing statistics for videoId: {}", videoId);

        // 중복 확인
        if (!videoStatisticsRepository.existsByVideoIdAndDate(videoId, yesterday)) {
            long totalPlayTime = playHistoryRepository.findPlayTimeByVideoIdAndDate(videoId, yesterday);
            long viewCount = playHistoryRepository.countByVideoIdAndDate(videoId, yesterday);
            long adViewCount = playHistoryRepository.countAdViewsByVideoIdAndDate(videoId, yesterday);

            return new VideoStatistics(videoId, yesterday, viewCount, totalPlayTime, adViewCount);
        } else {
            logger.info("Duplicate statistics data exists: video_id={}, date={}", videoId, yesterday);
            return null;
        }
    }
}
