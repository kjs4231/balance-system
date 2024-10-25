package com.example.balancesystem.global.config;

import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.video.VideoRepository;
import com.example.balancesystem.domain.videohistory.PlayHistory;
import com.example.balancesystem.domain.videostats.StatType;
import com.example.balancesystem.domain.videostats.VideoStatistics;
import com.example.balancesystem.domain.videostats.VideoStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.repeat.RepeatStatus;
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

    @Bean
    public Job monthStatisticsJob(JobRepository jobRepository, Step monthStatisticsStep) {
        return new org.springframework.batch.core.job.builder.JobBuilder("monthStatisticsJob", jobRepository)
                .start(monthStatisticsStep)
                .build();
    }

    @Bean
    public Step monthStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
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
                // 해당 월의 주간 통계 데이터를 가져와서 월간 통계 생성
                List<VideoStatistics> weeklyStatistics = videoStatisticsRepository
                        .findByVideoAndStatTypeAndDateBetween(video, StatType.WEEK, startOfMonth, endOfMonth);

                if (!weeklyStatistics.isEmpty()) {
                    Long totalViewCount = weeklyStatistics.stream()
                            .mapToLong(VideoStatistics::getViewCount)
                            .sum();
                    Long totalPlayTime = weeklyStatistics.stream()
                            .mapToLong(VideoStatistics::getTotalPlayTime)
                            .sum();

                    // VideoStatistics 생성 및 반환
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
