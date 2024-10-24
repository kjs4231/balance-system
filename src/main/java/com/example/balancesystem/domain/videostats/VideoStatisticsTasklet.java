package com.example.balancesystem.domain.videostats;

import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.video.VideoRepository;
import com.example.balancesystem.domain.videohistory.PlayHistory;
import com.example.balancesystem.domain.videohistory.PlayHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VideoStatisticsTasklet {

    private final VideoRepository videoRepository;
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final PlayHistoryRepository playHistoryRepository;

    // 특정 비디오의 총 재생 시간을 계산하는 메서드
    private Long calculateTotalPlayTime(Video video) {
        List<PlayHistory> playHistories = playHistoryRepository.findByVideo(video);
        return playHistories.stream()
                .mapToLong(PlayHistory::getPlayTime)
                .sum();
    }

    // 일간 통계 생성
    @Scheduled(cron = "0 0 0 * * ?")
    public void processStatisticsForDay() {
        LocalDate date = LocalDate.now();
        List<Video> videos = videoRepository.findAll();

        for (Video video : videos) {
            if (!videoStatisticsRepository.existsByVideoAndStatTypeAndDate(video, StatType.DAY, date)) {
                try {
                    Long viewCount = (long) video.getViewCount();
                    Long totalPlayTime = calculateTotalPlayTime(video);

                    VideoStatistics statistics = new VideoStatistics(video, StatType.DAY, date, viewCount, totalPlayTime);
                    videoStatisticsRepository.save(statistics);
                } catch (DataIntegrityViolationException e) {
                    System.out.println("이미 존재하는 통계 데이터입니다: video_id=" + video.getVideoId() + ", date=" + date);
                }
            } else {
                System.out.println("중복된 일간 통계 데이터가 감지되었습니다: video_id=" + video.getVideoId() + ", date=" + date);
            }
        }
    }

    // 주간 통계 생성
    @Scheduled(cron = "0 0 0 * * MON")
    public void processStatisticsForWeek() {
        LocalDate date = LocalDate.now();
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);

        List<Video> videos = videoRepository.findAll();
        for (Video video : videos) {
            if (!videoStatisticsRepository.existsByVideoAndStatTypeAndDate(video, StatType.WEEK, startOfWeek)) {
                List<VideoStatistics> dailyStatistics = videoStatisticsRepository
                        .findByVideoAndStatTypeAndDateBetween(video, StatType.DAY, startOfWeek, endOfWeek);

                if (!dailyStatistics.isEmpty()) {
                    Long totalViewCount = dailyStatistics.stream()
                            .mapToLong(VideoStatistics::getViewCount)
                            .sum();
                    Long totalPlayTime = dailyStatistics.stream()
                            .mapToLong(VideoStatistics::getTotalPlayTime)
                            .sum();

                    VideoStatistics statistics = new VideoStatistics(video, StatType.WEEK, startOfWeek, totalViewCount, totalPlayTime);
                    videoStatisticsRepository.save(statistics);
                }
            } else {
                System.out.println("중복된 주간 통계 데이터가 감지되었습니다: video_id=" + video.getVideoId() + ", week_start=" + startOfWeek);
            }
        }
    }

    // 월간 통계 생성
    @Scheduled(cron = "0 0 0 1 * ?")
    public void processStatisticsForMonth() {
        LocalDate date = LocalDate.now();
        YearMonth month = YearMonth.from(date);
        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        List<Video> videos = videoRepository.findAll();
        for (Video video : videos) {
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

                    VideoStatistics statistics = new VideoStatistics(video, StatType.MONTH, startOfMonth, totalViewCount, totalPlayTime);
                    videoStatisticsRepository.save(statistics);
                }
            } else {
                System.out.println("중복된 월간 통계 데이터가 감지되었습니다: video_id=" + video.getVideoId() + ", month=" + month);
            }
        }
    }
}
