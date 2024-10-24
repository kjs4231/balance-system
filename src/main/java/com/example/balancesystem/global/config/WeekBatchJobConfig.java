package com.example.balancesystem.global.config;

import com.example.balancesystem.domain.videostats.VideoStatisticsTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class WeekBatchJobConfig {

    private final VideoStatisticsTasklet videoStatisticsTasklet;

    @Bean
    public Job weekStatisticsJob(JobRepository jobRepository, Step weekStatisticsStep) {
        return new org.springframework.batch.core.job.builder.JobBuilder("weekStatisticsJob", jobRepository)
                .start(weekStatisticsStep)
                .build();
    }

    @Bean
    public Step weekStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("weekStatisticsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    videoStatisticsTasklet.processStatisticsForWeek();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
