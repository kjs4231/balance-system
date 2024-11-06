package com.example.batchservice.batch;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class partitioner implements Partitioner {

    private final List<Long> videoIds;

    public partitioner(List<Long> videoIds) {
        this.videoIds = videoIds;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        for (int i = 0; i < videoIds.size(); i++) {
            ExecutionContext context = new ExecutionContext();
            context.put("videoId", videoIds.get(i));
            partitions.put("partition" + i, context);
        }

        return partitions;
    }
}