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
import java.time.LocalDate;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DayBatchJobConfig {

    private final VideoStatisticsTasklet videoStatisticsTasklet;

    @Bean
    public Job dayStatisticsJob(JobRepository jobRepository, Step dayStatisticsStep) {
        return new org.springframework.batch.core.job.builder.JobBuilder("dayStatisticsJob", jobRepository)
                .start(dayStatisticsStep)
                .build();
    }

    @Bean
    public Step dayStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("dayStatisticsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate now = LocalDate.now();
                    videoStatisticsTasklet.processStatisticsForDay(now);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}