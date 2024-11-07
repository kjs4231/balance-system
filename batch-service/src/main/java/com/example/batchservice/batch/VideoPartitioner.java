package com.example.batchservice.batch;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoPartitioner implements Partitioner, Serializable {

    private final List<Long> videoIds;

    public VideoPartitioner(List<Long> videoIds) {
        this.videoIds = videoIds;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        int partitionSize = videoIds.size() / gridSize;

        for (int i = 0; i < gridSize; i++) {
            int start = i * partitionSize;
            int end = (i == gridSize - 1) ? videoIds.size() : (i + 1) * partitionSize;

            // SubList에서 ArrayList로 복사하여 Serializable 보장
            List<Long> partitionVideoIds = new ArrayList<>(videoIds.subList(start, end));
            ExecutionContext context = new ExecutionContext();
            context.put("videoIds", (Serializable) partitionVideoIds);
            partitions.put("partition" + i, context);
        }

        return partitions;
    }
}
