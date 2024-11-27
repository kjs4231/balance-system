package com.example.balancesystem.global.batch;

import com.example.balancesystem.domain.content.playhistory.dsl.PlayHistoryRepository;
import com.example.balancesystem.global.videostats.VideoStatistics;
import com.example.balancesystem.global.videostats.dsl.VideoStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public class DayStatisticsProcessor implements ItemProcessor<Long, VideoStatistics> {
    private static final Logger logger = LoggerFactory.getLogger(DayStatisticsProcessor.class);
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final PlayHistoryRepository playHistoryRepository;

    public DayStatisticsProcessor(VideoStatisticsRepository videoStatisticsRepository, PlayHistoryRepository playHistoryRepository) {
        this.videoStatisticsRepository = videoStatisticsRepository;
        this.playHistoryRepository = playHistoryRepository;
    }
    @Override
    //    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public VideoStatistics process(Long videoId) {
        long startTime = System.currentTimeMillis();
        LocalDate yesterday = LocalDate.now();
        logger.info("Processing statistics for videoId: {}", videoId);

        if (!videoStatisticsRepository.existsByVideoIdAndDate(videoId, yesterday)) {
            long totalPlayTime = playHistoryRepository.findPlayTimeByVideoIdAndDate(videoId, yesterday);
            long viewCount = playHistoryRepository.countByVideoIdAndDate(videoId, yesterday);
            long adViewCount = playHistoryRepository.countAdViewsByVideoIdAndDate(videoId, yesterday);

            long endTime = System.currentTimeMillis();
            logger.info("Statistics processing for videoId {} completed in {} ms", videoId, (endTime - startTime));

            return new VideoStatistics(videoId, yesterday, viewCount, totalPlayTime, adViewCount);
        } else {
            long endTime = System.currentTimeMillis();
            logger.info("Duplicate statistics data for videoId {} checked in {} ms", videoId, (endTime - startTime));
            return null;
        }
    }

}
