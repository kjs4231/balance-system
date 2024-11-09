package com.example.batchservice.batch;

import com.example.batchservice.videostats.VideoStatistics;
import com.example.batchservice.videostats.dsl.VideoStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.Chunk;

public class StatisticsWriter implements ItemWriter<VideoStatistics> {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsWriter.class);
    private final VideoStatisticsRepository videoStatisticsRepository;

    public StatisticsWriter(VideoStatisticsRepository videoStatisticsRepository) {
        this.videoStatisticsRepository = videoStatisticsRepository;
    }

    @Override
    public void write(Chunk<? extends VideoStatistics> items) {
        logger.info("size : {}", items.size());
        items.forEach(statistics -> {
            if (statistics != null) {
                logger.info("Writing statistics for videoId: {}", statistics.getVideoId());

                videoStatisticsRepository.save(statistics);
            }
        });
    }
}
