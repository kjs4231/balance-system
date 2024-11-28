package com.example.balancesystem.global.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.*;

public class VideoPartitioner implements Partitioner {

    private final List<Long> videoIds;
    private final Set<Long> processedVideoIds = new HashSet<>();

    public VideoPartitioner(List<Long> videoIds) {
        this.videoIds = videoIds;
    }

    private static final Logger logger = LoggerFactory.getLogger(VideoPartitioner.class);

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        int partitionSize = (int) Math.ceil((double) videoIds.size() / gridSize);

        for (int i = 0; i < gridSize; i++) {
            int fromIndex = i * partitionSize;
            int toIndex = Math.min(fromIndex + partitionSize, videoIds.size());

            if (fromIndex >= videoIds.size()) {
                logger.info("Skipping empty partition: {}", i);
                continue;
            }

            // 중복 방지: 이미 처리된 videoId 제외
            List<Long> partitionVideoIds = new ArrayList<>();
            for (Long videoId : videoIds.subList(fromIndex, toIndex)) {
                if (!processedVideoIds.contains(videoId)) {
                    partitionVideoIds.add(videoId);
                    processedVideoIds.add(videoId);
                }
            }

            if (partitionVideoIds.isEmpty()) {
                logger.info("Skipping empty partition due to duplicate filtering: {}", i);
                continue;
            }

            ExecutionContext context = new ExecutionContext();
            context.put("videoIds", partitionVideoIds);
            partitions.put("partition" + i, context);

            logger.info("Partition {}: videoIds: {}", i, partitionVideoIds);
        }

        return partitions;
    }
}

