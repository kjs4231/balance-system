package com.example.balancesystem.global.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import java.time.LocalDateTime;

public class DayBatchJobListener implements JobExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(DayBatchJobListener.class);
    private long jobStartTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobStartTime = System.currentTimeMillis();
        logger.info("Job dayStatisticsJob started at: {}", LocalDateTime.now());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long jobEndTime = System.currentTimeMillis();
        logger.info("Job dayStatisticsJob completed at: {}", LocalDateTime.now());
        logger.info("Total time taken for dayStatisticsJob: {} ms", (jobEndTime - jobStartTime));
    }
}
