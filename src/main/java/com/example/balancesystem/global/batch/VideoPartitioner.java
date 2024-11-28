package com.example.balancesystem.global.batch;

import com.example.balancesystem.domain.content.video.dsl.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class VideoPartitioner implements Partitioner {

    private final VideoRepository videoRepository;
    private static final Logger logger = LoggerFactory.getLogger(VideoPartitioner.class);

    public VideoPartitioner(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Long minId = videoRepository.getMinId();
        Long maxId = videoRepository.getMaxId();

        if (minId == null || maxId == null) {
            logger.warn("No video IDs found in the database.");
            return Collections.emptyMap();
        }

        long range = maxId - minId + 1;
        long targetSize = Math.max(1, range / gridSize);

        Map<String, ExecutionContext> partitions = new HashMap<>();
        long start = minId;
        long end = start + targetSize - 1;

        for (int i = 0; i < gridSize; i++) {
            if (start > maxId) break;

            ExecutionContext context = new ExecutionContext();
            context.putLong("minId", start);
            context.putLong("maxId", Math.min(end, maxId));

            partitions.put("partition" + i, context);
            logger.info("Partition {}: minId={}, maxId={}", i, start, Math.min(end, maxId));

            start = end + 1;
            end = start + targetSize - 1;
        }

        logger.info("Partitioning completed: {} partitions created.", partitions.size());
        return partitions;
    }
}
