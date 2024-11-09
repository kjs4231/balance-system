package com.example.batchservice.batch;

import com.example.batchservice.videorevenue.VideoRevenue;
import com.example.batchservice.videorevenue.dsl.VideoRevenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.Chunk;

public class RevenueWriter implements ItemWriter<VideoRevenue> {
    private static final Logger logger = LoggerFactory.getLogger(RevenueWriter.class);
    private final VideoRevenueRepository videoRevenueRepository;

    public RevenueWriter(VideoRevenueRepository videoRevenueRepository) {
        this.videoRevenueRepository = videoRevenueRepository;
    }

    @Override
    public void write(Chunk<? extends VideoRevenue> items) {
        logger.info("size : {}", items.size());
        items.forEach(revenue -> {
            if (revenue != null) {
                logger.info("Writing revenue for videoId: {}", revenue.getVideoId());
                videoRevenueRepository.save(revenue);
            }
        });
    }
}
