package com.example.balancesystem.global.config;

import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.video.VideoRepository;
import com.example.balancesystem.domain.videostats.StatType;
import com.example.balancesystem.domain.videostats.VideoStatistics;
import com.example.balancesystem.domain.videostats.VideoStatisticsRepository;
import com.example.balancesystem.domain.videostats.VideoStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MonthBatchJobConfig {

    private final VideoRepository videoRepository;
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final VideoStatisticsService videoStatisticsService;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job monthStatisticsJob() {
        return new JobBuilder("monthStatisticsJob", jobRepository)
                .start(monthStatisticsStep())
                .build();
    }

    @Bean
    public Step monthStatisticsStep() {
        return new StepBuilder("monthStatisticsStep", jobRepository)
                .<Video, VideoStatistics>chunk(10, transactionManager)
                .reader(monthVideoReader())
                .processor(monthVideoProcessor())
                .writer(monthVideoStatisticsWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Video> monthVideoReader() {
        RepositoryItemReader<Video> reader = new RepositoryItemReader<>();
        reader.setRepository(videoRepository);
        reader.setMethodName("findAll");
        reader.setSort(Collections.singletonMap("videoId", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<Video, VideoStatistics> monthVideoProcessor() {
        return video -> {
            LocalDate now = LocalDate.now();
            YearMonth month = YearMonth.from(now);
            LocalDate startOfMonth = month.atDay(1);
            LocalDate endOfMonth = month.atEndOfMonth();

            // 월간 통계 데이터가 이미 존재하는지 확인
            if (!videoStatisticsRepository.existsByVideoAndStatTypeAndDate(video, StatType.MONTH, startOfMonth)) {
                List<VideoStatistics> weeklyStatistics = videoStatisticsRepository
                        .findByVideoAndStatTypeAndDateBetween(video, StatType.WEEK, startOfMonth, endOfMonth);

                if (!weeklyStatistics.isEmpty()) {
                    Long totalViewCount = weeklyStatistics.stream()
                            .mapToLong(VideoStatistics::getViewCount)
                            .sum();
                    Long totalPlayTime = weeklyStatistics.stream()
                            .mapToLong(VideoStatistics::getTotalPlayTime)
                            .sum();

                    return new VideoStatistics(video, StatType.MONTH, startOfMonth, totalViewCount, totalPlayTime);
                }
            }
            return null;
        };
    }

    @Bean
    public ItemWriter<VideoStatistics> monthVideoStatisticsWriter() {
        return items -> {
            for (VideoStatistics statistics : items) {
                if (statistics != null) {
                    videoStatisticsRepository.save(statistics);
                }
            }
        };
    }
}
