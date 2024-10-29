package com.example.balancesystem.global.config;

import com.example.balancesystem.domain.revenuerate.RevenueRate;
import com.example.balancesystem.domain.revenuerate.RevenueRateRepository;
import com.example.balancesystem.domain.revenuerate.RevenueType;
import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.video.VideoRepository;
import com.example.balancesystem.domain.videorevenue.VideoRevenue;
import com.example.balancesystem.domain.videorevenue.VideoRevenueRepository;
import com.example.balancesystem.domain.videostats.VideoStatistics;
import com.example.balancesystem.domain.videostats.VideoStatisticsRepository;
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
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableScheduling
public class DayBatchJobConfig {

    private final VideoRepository videoRepository;
    private final VideoRevenueRepository videoRevenueRepository;
    private final RevenueRateRepository revenueRateRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dayStatisticsJob() {
        return new JobBuilder("dayStatisticsJob", jobRepository)
                .start(dayRevenueStep())
                .build();
    }

    @Bean
    public Step dayRevenueStep() {
        return new StepBuilder("dayRevenueStep", jobRepository)
                .<Video, VideoRevenue>chunk(10, transactionManager)
                .reader(videoReader())
                .processor(revenueProcessor())
                .writer(revenueWriter())
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
    public ItemProcessor<Video, VideoRevenue> revenueProcessor() {
        return video -> {
            LocalDate today = LocalDate.now();

            // 누적 조회수와 광고 조회수를 Video 엔티티에서 직접 가져옴
            long totalViewCount = video.getViewCount();
            long totalAdViewCount = video.getAdViewCount();

            // 오늘의 조회수를 위한 전날 기준의 누적 조회수를 계산
            long dailyViewCount = video.getVideoStatistics().stream()
                    .filter(stat -> stat.getDate().equals(today.minusDays(1)))
                    .mapToLong(VideoStatistics::getViewCount)
                    .sum();

            long dailyAdViewCount = video.getVideoStatistics().stream()
                    .filter(stat -> stat.getDate().equals(today.minusDays(1)))
                    .mapToLong(VideoStatistics::getAdViewCount)
                    .sum();

            // 전체 조회수에 따른 정산 금액 계산
            BigDecimal viewRevenue = calculateRevenue(totalViewCount, dailyViewCount, RevenueType.VIDEO);
            BigDecimal adRevenue = calculateRevenue(totalAdViewCount, dailyAdViewCount, RevenueType.AD);
            BigDecimal totalRevenue = viewRevenue.add(adRevenue);

            return new VideoRevenue(video, today, viewRevenue, adRevenue, totalRevenue);
        };
    }

    private BigDecimal calculateRevenue(long totalViews, long dailyViews, RevenueType revenueType) {
        BigDecimal revenue = BigDecimal.ZERO;
        long remainingViews = dailyViews;

        // 구간별로 RevenueRate를 가져와서 계산
        List<RevenueRate> rates = revenueRateRepository.findAllByTypeOrderByMinViewsAsc(revenueType); // 구간을 정렬하여 가져옴
        long cumulativeViews = totalViews - dailyViews; // 오늘 조회수 이전의 누적 조회수

        for (RevenueRate rate : rates) {
            // 해당 구간의 시작 조회수와 끝 조회수를 기준으로 현재 구간의 적용 가능한 조회수를 계산
            long minViews = rate.getMinViews() != null ? rate.getMinViews() : 0;
            long maxViews = rate.getMaxViews() != null ? rate.getMaxViews() : Long.MAX_VALUE;

            // 누적 조회수와 오늘 조회수를 통해 해당 구간에서 적용할 조회수 계산
            long viewsInThisRange = Math.max(0, Math.min(remainingViews, maxViews - Math.max(cumulativeViews, minViews)));

            // 단가 적용 및 정산 금액 계산
            BigDecimal calculatedAmount = BigDecimal.valueOf(rate.getRate()).multiply(BigDecimal.valueOf(viewsInThisRange));
            revenue = revenue.add(calculatedAmount);

            System.out.println("구간: " + minViews + " ~ " + (maxViews != Long.MAX_VALUE ? maxViews : "무제한") +
                    ", 단가: " + rate.getRate() + ", 적용된 조회수: " + viewsInThisRange + ", 계산된 금액: " + calculatedAmount);

            // 남은 조회수 및 누적 조회수 업데이트
            remainingViews -= viewsInThisRange;
            cumulativeViews += viewsInThisRange;

            if (remainingViews <= 0) break;
        }

        System.out.println("총 정산 금액: " + revenue.setScale(0, RoundingMode.DOWN));
        System.out.println("=== 정산 금액 계산 완료 ===\n");

        return revenue.setScale(0, RoundingMode.DOWN);
    }



    @Bean
    public ItemWriter<VideoRevenue> revenueWriter() {
        return revenues -> {
            for (VideoRevenue revenue : revenues) {
                videoRevenueRepository.save(revenue);
            }
        };
    }
}
