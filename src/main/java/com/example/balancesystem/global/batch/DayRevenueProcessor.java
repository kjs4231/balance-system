package com.example.balancesystem.global.batch;

import com.example.balancesystem.domain.content.video.dsl.VideoRepository;
import com.example.balancesystem.domain.content.playhistory.dsl.PlayHistoryRepository;
import com.example.balancesystem.global.revenuerate.RevenueRate;
import com.example.balancesystem.global.revenuerate.RevenueType;
import com.example.balancesystem.global.revenuerate.dsl.RevenueRateRepository;
import com.example.balancesystem.global.videorevenue.VideoRevenue;
import com.example.balancesystem.global.videorevenue.dsl.VideoRevenueRepository;
import com.example.balancesystem.global.videostats.dsl.VideoStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DayRevenueProcessor implements ItemProcessor<Long, VideoRevenue> {
    private static final Logger logger = LoggerFactory.getLogger(DayRevenueProcessor.class);
    private final VideoRepository videoRepository;
    private final VideoRevenueRepository videoRevenueRepository;
    private final RevenueRateRepository revenueRateRepository;
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final PlayHistoryRepository playHistoryRepository;
    private final ConcurrentHashMap<RevenueType, List<RevenueRate>> revenueRateCache;

    public DayRevenueProcessor(VideoRepository videoRepository,
                               VideoRevenueRepository videoRevenueRepository,
                               RevenueRateRepository revenueRateRepository,
                               VideoStatisticsRepository videoStatisticsRepository, PlayHistoryRepository playHistoryRepository,
                               ConcurrentHashMap<RevenueType, List<RevenueRate>> revenueRateCache) {
        this.videoRepository = videoRepository;
        this.videoRevenueRepository = videoRevenueRepository;
        this.revenueRateRepository = revenueRateRepository;
        this.videoStatisticsRepository = videoStatisticsRepository;
        this.playHistoryRepository = playHistoryRepository;
        this.revenueRateCache = revenueRateCache;
    }

    @Override
    public VideoRevenue process(Long videoId) {
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
    }

    private BigDecimal calculateRevenue(long totalViews, long dailyViews, RevenueType revenueType) {
        BigDecimal revenue = BigDecimal.ZERO;
        long remainingViews = dailyViews;
        List<RevenueRate> rates = revenueRateCache.computeIfAbsent(revenueType,
                type -> revenueRateRepository.findAllByTypeOrderByMinViewsAsc(type));
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
        return revenue.setScale(2, RoundingMode.DOWN);
    }
}
