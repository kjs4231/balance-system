package com.example.batchservice.batch;

import com.example.contentservice.video.VideoRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;
import java.util.Collections;

public class VideoIdReader extends RepositoryItemReader<Long> {
    public VideoIdReader(VideoRepository videoRepository) {
        this.setRepository(videoRepository);
        this.setMethodName("findAllVideoIds");
        this.setSort(Collections.singletonMap("videoId", Sort.Direction.ASC));
    }
}
