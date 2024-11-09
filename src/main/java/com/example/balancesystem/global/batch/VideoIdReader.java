package com.example.balancesystem.global.batch;

import com.example.balancesystem.domain.content.video.dsl.VideoRepository;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

public class VideoIdReader implements ItemReader<Long>, StepExecutionListener {

    private final VideoRepository videoRepository;
    private Iterator<Long> videoIdIterator;

    public VideoIdReader(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        List<Long> videoIds = (List<Long>) executionContext.get("videoIds");
        if (videoIds != null) {
            this.videoIdIterator = videoIds.iterator();
            System.out.println("Received videoIds for partition: " + videoIds);
        }
    }

    @Override
    public Long read() {
        return (videoIdIterator != null && videoIdIterator.hasNext()) ? videoIdIterator.next() : null;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
