package com.example.balancesystem.global.batch;


import com.example.balancesystem.domain.content.video.dsl.VideoRepository;
import com.example.balancesystem.domain.content.videohistory.dsl.PlayHistoryRepository;
import com.example.balancesystem.global.revenuerate.RevenueRate;
import com.example.balancesystem.global.revenuerate.RevenueType;
import com.example.balancesystem.global.revenuerate.dsl.RevenueRateRepository;
import com.example.balancesystem.global.videorevenue.VideoRevenue;
import com.example.balancesystem.global.videorevenue.dsl.VideoRevenueRepository;
import com.example.balancesystem.global.videostats.VideoStatistics;
import com.example.balancesystem.global.videostats.dsl.VideoStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableScheduling
public class DayBatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final VideoRepository videoRepository;
    private final VideoRevenueRepository videoRevenueRepository;
    private final RevenueRateRepository revenueRateRepository;
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final PlayHistoryRepository playHistoryRepository;
    private final RevenueWriter revenueWriter;
    private final StatisticsWriter statisticsWriter;

    private final ConcurrentHashMap<RevenueType, List<RevenueRate>> revenueRateCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> cachedPlayTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> cachedViewCount = new ConcurrentHashMap<>();

    @Bean
    public Job dayStatisticsJob() {
        return new JobBuilder("dayStatisticsJob", jobRepository)
                .incrementer(parameters -> new JobParametersBuilder(parameters)
                        .addString("date", LocalDate.now().toString())
                        .toJobParameters())
                .start(partitionedDayStatisticsStep())
                .next(partitionedDayRevenueStep())
                .listener(new DayBatchJobListener())
                .build();
    }

    @Bean
    public Step partitionedDayStatisticsStep() {
        return new StepBuilder("partitionedDayStatisticsStep", jobRepository)
                .partitioner(dayStatisticsStep().getName(), new VideoPartitioner(videoRepository.findAllVideoIds()))
                .partitionHandler(statisticsPartitionHandler())
                .build();
    }

    @Bean
    public Step partitionedDayRevenueStep() {
        return new StepBuilder("partitionedDayRevenueStep", jobRepository)
                .partitioner(dayRevenueStep().getName(), new VideoPartitioner(videoRepository.findAllVideoIds()))
                .partitionHandler(revenuePartitionHandler())
                .build();
    }

    @Bean
    public Step dayStatisticsStep() {
        return new StepBuilder("dayStatisticsStep", jobRepository)
                .<Long, VideoStatistics>chunk(10, transactionManager)
                .reader(videoIdReader(videoRepository))
                .processor(new DayStatisticsProcessor(videoStatisticsRepository, playHistoryRepository, cachedPlayTime, cachedViewCount))
                .writer(statisticsWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(3)
                .retry(Exception.class)
                .retryLimit(2)
                .build();
    }

    @Bean
    public Step dayRevenueStep() {
        return new StepBuilder("dayRevenueStep", jobRepository)
                .<Long, VideoRevenue>chunk(10, transactionManager)
                .reader(videoIdReader(videoRepository))
                .processor(new DayRevenueProcessor(videoRepository, videoRevenueRepository, revenueRateRepository, videoStatisticsRepository, playHistoryRepository, revenueRateCache))
                .writer(revenueWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(3)
                .retry(Exception.class)
                .retryLimit(2)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler statisticsPartitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(dayStatisticsStep());
        handler.setTaskExecutor(threadPoolTaskExecutor());
        handler.setGridSize(4);
        return handler;
    }

    @Bean
    public TaskExecutorPartitionHandler revenuePartitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(dayRevenueStep());
        handler.setTaskExecutor(threadPoolTaskExecutor());
        handler.setGridSize(4);
        return handler;
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("BatchExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    @StepScope
    public VideoIdReader videoIdReader(VideoRepository videoRepository) {
        return new VideoIdReader(videoRepository);
    }
}
