package com.example.balancesystem.global.config;

import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.video.VideoRepository;
import com.example.balancesystem.domain.videohistory.PlayHistory;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableScheduling
public class DayBatchJobConfig {

    private final VideoRepository videoRepository;
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final VideoStatisticsService videoStatisticsService;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dayStatisticsJob() {
        return new JobBuilder("dayStatisticsJob", jobRepository)
                .start(dayStatisticsStep())
                .build();
    }

    @Bean
    public Step dayStatisticsStep() {
        return new StepBuilder("dayStatisticsStep", jobRepository)
                .<Video, VideoStatistics>chunk(10, transactionManager)
                .reader(videoReader())
                .processor(videoProcessor())
                .writer(dayVideoStatisticsWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Video> videoReader() {
        RepositoryItemReader<Video> reader = new RepositoryItemReader<>();
        reader.setRepository(videoRepository);
        reader.setMethodName("findAll");
        reader.setSort(Collections.singletonMap("videoId", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<Video, VideoStatistics> videoProcessor() {
        return video -> {
            LocalDate today = LocalDate.now(); // 오늘 날짜만 처리

            // 오늘 날짜에 대한 통계 데이터가 이미 존재하는지 확인
            if (!videoStatisticsRepository.existsByVideoAndStatTypeAndDate(video, StatType.DAY, today)) {
                // 오늘 날짜에 해당하는 PlayHistory 데이터를 필터링
                Long totalPlayTime = video.getPlayHistories().stream()
                        .filter(playHistory -> playHistory.getViewDate().toLocalDate().equals(today)) // 오늘 날짜의 데이터만 필터링
                        .mapToLong(PlayHistory::getPlayTime)
                        .sum();

                Long viewCount = video.getPlayHistories().stream()
                        .filter(playHistory -> playHistory.getViewDate().toLocalDate().equals(today)) // 오늘 날짜의 조회수 계산
                        .count();

                // VideoStatistics 생성 및 반환
                return new VideoStatistics(video, StatType.DAY, today, viewCount, totalPlayTime);
            } else {
                System.out.println("중복된 일간 통계 데이터가 감지되었습니다: video_id=" + video.getVideoId() + ", date=" + today);
                return null;
            }
        };
    }

    @Bean
    public ItemWriter<VideoStatistics> dayVideoStatisticsWriter() {
        return items -> {
            for (VideoStatistics statistics : items) {
                if (statistics != null) {
                    videoStatisticsRepository.save(statistics);
                }
            }
        };
    }
}
