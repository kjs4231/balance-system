package com.example.balancesystem.global.batch;

import com.example.balancesystem.domain.content.video.dsl.VideoRepository;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class VideoIdReader implements ItemReader<Long>, StepExecutionListener {

    private final VideoRepository videoRepository;
    private Iterator<Long> videoIdIterator;

    public VideoIdReader(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        long minId = stepExecution.getExecutionContext().getLong("minId");
        long maxId = stepExecution.getExecutionContext().getLong("maxId");

        List<Long> videoIds = videoRepository.findVideoIdsByRange(minId, maxId);
        this.videoIdIterator = videoIds.iterator();
    }

    @Override
    public Long read() {
        return (videoIdIterator != null && videoIdIterator.hasNext()) ? videoIdIterator.next() : null;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Cleanup if needed or additional logic
        return ExitStatus.COMPLETED;
    }
}
