package com.example.balancesystem.global.batch;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoPartitioner implements Partitioner {

    private final List<Long> videoIds;

    public VideoPartitioner(List<Long> videoIds) {
        this.videoIds = videoIds;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        int partitionSize = (int) Math.ceil((double) videoIds.size() / gridSize);

        for (int i = 0; i < gridSize; i++) {
            int fromIndex = i * partitionSize;
            int toIndex = Math.min(fromIndex + partitionSize, videoIds.size());

            if (fromIndex >= videoIds.size()) {
                break;
            }

            List<Long> partitionVideoIds = new ArrayList<>(videoIds.subList(fromIndex, toIndex));
            ExecutionContext context = new ExecutionContext();
            context.put("videoIds", partitionVideoIds);
            partitions.put("partition" + i, context);

            System.out.println("파티션 " + i + ", videoIds: " + partitionVideoIds);
        }

        return partitions;
    }
}