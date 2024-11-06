package com.example.batchservice.batch;

import com.example.batchservice.revenuerate.RevenueRate;
import com.example.batchservice.revenuerate.RevenueRateRepository;
import com.example.batchservice.revenuerate.RevenueType;
import com.example.batchservice.videorevenue.VideoRevenue;
import com.example.batchservice.videorevenue.VideoRevenueRepository;
import com.example.batchservice.videostats.VideoStatistics;
import com.example.batchservice.videostats.VideoStatisticsRepository;
import com.example.contentservice.video.VideoRepository;
import com.example.contentservice.videohistory.PlayHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableScheduling
public class DayBatchJobConfig {

    private static final Logger logger = LoggerFactory.getLogger(DayBatchJobConfig.class);

    private final VideoRepository videoRepository;
    private final VideoRevenueRepository videoRevenueRepository;
    private final RevenueRateRepository revenueRateRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final PlayHistoryRepository playHistoryRepository;
    private final ConcurrentHashMap<RevenueType, List<RevenueRate>> revenueRateCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> cachedPlayTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> cachedViewCount = new ConcurrentHashMap<>();

    @Bean
    public Job dayStatisticsJob() {
        return new JobBuilder("dayStatisticsJob", jobRepository)
                .incrementer(parameters -> {
                    String date = LocalDate.now().toString();
                    return new JobParametersBuilder(parameters)
                            .addString("date", date)
                            .toJobParameters();
                })
                .start(partitionedDayStatisticsStep())
                .next(partitionedDayRevenueStep())
                .listener(new JobExecutionListener() {
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
                })
                .build();
    }

    @Bean
    public Step partitionedDayStatisticsStep() {
        List<Long> videoIds = videoRepository.findAllVideoIds();
        return new StepBuilder("partitionedDayStatisticsStep", jobRepository)
                .partitioner(dayStatisticsStep().getName(), new partitioner(videoIds))
                .partitionHandler(statisticsPartitionHandler())
                .build();
    }

    @Bean
    public Step partitionedDayRevenueStep() {
        List<Long> videoIds = videoRepository.findAllVideoIds();
        return new StepBuilder("partitionedDayRevenueStep", jobRepository)
                .partitioner(dayRevenueStep().getName(), new partitioner(videoIds))
                .partitionHandler(revenuePartitionHandler())
                .build();
    }

    @Bean
    public Step dayStatisticsStep() {
        return new StepBuilder("dayStatisticsStep", jobRepository)
                .<Long, VideoStatistics>chunk(10, transactionManager)
                .reader(videoIdReader())
                .processor(statisticsProcessor())
                .writer(statisticsWriter())
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
                .reader(videoIdReader())
                .processor(revenueProcessor())
                .writer(revenueWriter())
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
    public RepositoryItemReader<Long> videoIdReader() {
        RepositoryItemReader<Long> reader = new RepositoryItemReader<>();
        reader.setRepository(videoRepository);
        reader.setMethodName("findAllVideoIds");
        reader.setSort(Collections.singletonMap("videoId", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<Long, VideoStatistics> statisticsProcessor() {
        return videoId -> {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            logger.info("Processing statistics for videoId: {}", videoId);
            if (!videoStatisticsRepository.existsByVideoIdAndDate(videoId, yesterday)) {
                long totalPlayTime = getCachedPlayTime(videoId, yesterday);
                long viewCount = getCachedViewCount(videoId, yesterday);
                long adViewCount = playHistoryRepository.countAdViewsByVideoIdAndDate(videoId, yesterday);
                return new VideoStatistics(videoId, yesterday, viewCount, totalPlayTime, adViewCount);
            } else {
                logger.info("Duplicate statistics data exists: video_id={}, date=", videoId, yesterday);
                return null;
            }
        };
    }

    @Bean
    public ItemWriter<VideoStatistics> statisticsWriter() {
        return items -> items.forEach(statistics -> {
            if (statistics != null) {
                logger.info("Writing statistics for videoId: {}", statistics.getVideoId());
                videoStatisticsRepository.save(statistics);
            }
        });
    }

    @Bean
    public ItemProcessor<Long, VideoRevenue> revenueProcessor() {
        return videoId -> {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            logger.info("Processing revenue for videoId: {}", videoId);

            if (!videoRevenueRepository.existsByVideoIdAndDate(videoId, yesterday)) {
                long totalViewCount = videoRepository.getViewCountByVideoId(videoId);
                long totalAdViewCount = videoRepository.getAdViewCountByVideoId(videoId);
                long dailyViewCount = videoStatisticsRepository.getDailyViewCountByVideoId(videoId, yesterday);
                long dailyAdViewCount = videoStatisticsRepository.getDailyAdViewCountByVideoId(videoId, yesterday);

                BigDecimal viewRevenue = calculateRevenue(totalViewCount, dailyViewCount, RevenueType.VIDEO);
                BigDecimal adRevenue = calculateRevenue(totalAdViewCount, dailyAdViewCount, RevenueType.AD);
                BigDecimal totalRevenue = viewRevenue.add(adRevenue);

                return new VideoRevenue(videoId, yesterday, viewRevenue, adRevenue, totalRevenue);
            } else {
                logger.info("Duplicate revenue data exists: video_id={}, date={}", videoId, yesterday);
                return null;
            }
        };
    }

    @Bean
    public ItemWriter<VideoRevenue> revenueWriter() {
        return items -> items.forEach(revenue -> {
            if (revenue != null) {
                logger.info("Writing revenue for videoId: {}", revenue.getVideoId());
                videoRevenueRepository.save(revenue);
            }
        });
    }

    private long getCachedPlayTime(Long videoId, LocalDate date) {
        return cachedPlayTime.computeIfAbsent(videoId, id -> playHistoryRepository.findPlayTimeByVideoIdAndDate(id, date));
    }

    private long getCachedViewCount(Long videoId, LocalDate date) {
        return cachedViewCount.computeIfAbsent(videoId, id -> playHistoryRepository.countByVideoIdAndDate(id, date));
    }

    private BigDecimal calculateRevenue(long totalViews, long dailyViews, RevenueType revenueType) {
        BigDecimal revenue = BigDecimal.ZERO;
        long remainingViews = dailyViews;
        List<RevenueRate> rates = getRevenueRates(revenueType);
        long cumulativeViews = totalViews - dailyViews;

        for (RevenueRate rate : rates) {
            long minViews = rate.getMinViews() != null ? rate.getMinViews() : 0;
            long maxViews = rate.getMaxViews() != null ? rate.getMaxViews() : Long.MAX_VALUE;
            long viewsInThisRange = Math.max(0, Math.min(remainingViews, maxViews - Math.max(cumulativeViews, minViews)));
            BigDecimal calculatedAmount = BigDecimal.valueOf(rate.getRate()).multiply(BigDecimal.valueOf(viewsInThisRange));
            revenue = revenue.add(calculatedAmount);

            remainingViews -= viewsInThisRange;
            cumulativeViews += viewsInThisRange;
            if (remainingViews <= 0) break;
        }
        return revenue.setScale(0, RoundingMode.DOWN);
    }

    private List<RevenueRate> getRevenueRates(RevenueType revenueType) {
        return revenueRateCache.computeIfAbsent(revenueType, type -> revenueRateRepository.findAllByTypeOrderByMinViewsAsc(type));
    }
}
