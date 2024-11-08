package com.example.batchservice.batch;

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

            // fromIndex가 리스트 크기를 초과하면 루프를 빠져나갑니다.
            if (fromIndex >= videoIds.size()) {
                break;
            }

            // Serializable을 보장하기 위해 ArrayList로 생성
            List<Long> partitionVideoIds = new ArrayList<>(videoIds.subList(fromIndex, toIndex));
            ExecutionContext context = new ExecutionContext();
            context.put("videoIds", partitionVideoIds);
            partitions.put("partition" + i, context);
        }

        return partitions;
    }

}
