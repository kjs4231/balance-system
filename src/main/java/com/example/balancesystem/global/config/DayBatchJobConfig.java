package com.example.balancesystem.global.config;

import com.example.balancesystem.domain.revenuerate.RevenueRate;
import com.example.balancesystem.domain.revenuerate.RevenueRateRepository;
import com.example.balancesystem.domain.revenuerate.RevenueType;
import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.video.VideoRepository;
import com.example.balancesystem.domain.videohistory.PlayHistory;
import com.example.balancesystem.domain.videorevenue.VideoRevenue;
import com.example.balancesystem.domain.videorevenue.VideoRevenueRepository;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableScheduling
public class DayBatchJobConfig {

    private final VideoRepository videoRepository;
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final VideoRevenueRepository videoRevenueRepository;
    private final RevenueRateRepository revenueRateRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dayStatisticsJob() {
        return new JobBuilder("dayStatisticsJob", jobRepository)
                .start(dayStatisticsStep())
                .next(dayRevenueStep())
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
    public Step dayRevenueStep() {
        return new StepBuilder("dayRevenueStep", jobRepository)
                .<VideoStatistics, VideoRevenue>chunk(10, transactionManager) // 명시적인 제네릭 타입 지정
                .reader(revenueReader())
                .processor(revenueProcessor())
                .writer(revenueWriter())
                .build();
    }


    private ItemReader<VideoStatistics> revenueReader() {
        RepositoryItemReader<VideoStatistics> reader = new RepositoryItemReader<>();
        reader.setRepository(videoStatisticsRepository);
        reader.setMethodName("findByDate");
        reader.setArguments(Collections.singletonList(LocalDate.now().minusDays(1)));
        reader.setPageSize(10);
        reader.setSort(Collections.singletonMap("video.videoId", Sort.Direction.ASC));
        return reader;
    }


    @Bean
    public ItemProcessor<VideoStatistics, VideoRevenue> revenueProcessor() {
        return videoStatistics -> {
            long viewCount = videoStatistics.getViewCount();
            long adViewCount = videoStatistics.getAdViewCount();


            if (videoRevenueRepository.existsByVideoAndDate(videoStatistics.getVideo(), videoStatistics.getDate())) {
                System.out.println("중복된 정산 데이터가 이미 존재합니다: video_id=" + videoStatistics.getVideo().getVideoId() + ", date=" + videoStatistics.getDate());
                return null;
            }


            RevenueRate viewRate = revenueRateRepository.findRateByViewsAndType(viewCount, RevenueType.VIDEO);
            RevenueRate adRate = revenueRateRepository.findRateByViewsAndType(adViewCount, RevenueType.AD);

            BigDecimal viewRevenue = BigDecimal.valueOf(viewRate.getRate()).multiply(BigDecimal.valueOf(viewCount)).setScale(0, RoundingMode.DOWN);
            BigDecimal adRevenue = BigDecimal.valueOf(adRate.getRate()).multiply(BigDecimal.valueOf(adViewCount)).setScale(0, RoundingMode.DOWN);
            BigDecimal totalRevenue = viewRevenue.add(adRevenue);

            return new VideoRevenue(videoStatistics.getVideo(), videoStatistics.getDate(), viewRevenue, adRevenue, totalRevenue);
        };
    }



    @Bean
    public ItemWriter<VideoRevenue> revenueWriter() {
        return revenues -> {
            for (VideoRevenue revenue : revenues) {
                videoRevenueRepository.save(revenue);
            }
        };
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
            LocalDate yesterday = LocalDate.now().minusDays(1);

            // 어제 날짜에 대한 통계 데이터가 이미 존재하는지 확인
            if (!videoStatisticsRepository.existsByVideoAndDate(video, yesterday)) {
                // 어제 날짜에 해당하는 PlayHistory 데이터를 필터링
                Long totalPlayTime = video.getPlayHistories().stream()
                        .filter(playHistory -> playHistory.getViewDate().toLocalDate().equals(yesterday))
                        .mapToLong(PlayHistory::getPlayTime)
                        .sum();

                Long viewCount = video.getPlayHistories().stream()
                        .filter(playHistory -> playHistory.getViewDate().toLocalDate().equals(yesterday))
                        .count();

                Long adViewCount = video.getAdHistories().stream()
                        .filter(adHistory -> adHistory.getViewDate().equals(yesterday))
                        .count();

                return new VideoStatistics(video, yesterday, viewCount, totalPlayTime, adViewCount);
            } else {
                System.out.println("중복된 통계 데이터가 이미 존재합니다: video_id=" + video.getVideoId() + ", date=" + yesterday);
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
