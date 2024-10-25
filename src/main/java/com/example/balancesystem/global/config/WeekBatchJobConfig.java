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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class WeekBatchJobConfig {

    private final VideoRepository videoRepository;
    private final VideoStatisticsRepository videoStatisticsRepository;

    @Bean
    public Job weekStatisticsJob(JobRepository jobRepository, Step weekStatisticsStep) {
        return new org.springframework.batch.core.job.builder.JobBuilder("weekStatisticsJob", jobRepository)
                .start(weekStatisticsStep)
                .build();
    }

    @Bean
    public Step weekStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("weekStatisticsStep", jobRepository)
                .<Video, VideoStatistics>chunk(10, transactionManager)
                .reader(weekVideoReader())
                .processor(weekVideoProcessor())
                .writer(weekVideoStatisticsWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Video> weekVideoReader() {
        RepositoryItemReader<Video> reader = new RepositoryItemReader<>();
        reader.setRepository(videoRepository);
        reader.setMethodName("findAll");
        reader.setSort(Collections.singletonMap("videoId", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<Video, VideoStatistics> weekVideoProcessor() {
        return video -> {
            LocalDate now = LocalDate.now();
            LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);

            // 주간 통계 데이터가 이미 존재하는지 확인
            if (!videoStatisticsRepository.existsByVideoAndStatTypeAndDate(video, StatType.WEEK, startOfWeek)) {
                // 해당 주의 일간 통계 데이터를 가져와서 주간 통계 생성
                List<VideoStatistics> dailyStatistics = videoStatisticsRepository
                        .findByVideoAndStatTypeAndDateBetween(video, StatType.DAY, startOfWeek, endOfWeek);

                if (!dailyStatistics.isEmpty()) {
                    Long totalViewCount = dailyStatistics.stream()
                            .mapToLong(VideoStatistics::getViewCount)
                            .sum();
                    Long totalPlayTime = dailyStatistics.stream()
                            .mapToLong(VideoStatistics::getTotalPlayTime)
                            .sum();

                    // VideoStatistics 생성 및 반환
                    return new VideoStatistics(video, StatType.WEEK, startOfWeek, totalViewCount, totalPlayTime);
                }
            }
            return null;
        };
    }



    @Bean
    public ItemWriter<VideoStatistics> weekVideoStatisticsWriter() {
        return items -> {
            for (VideoStatistics statistics : items) {
                if (statistics != null) {
                    videoStatisticsRepository.save(statistics);
                }
            }
        };
    }
}
