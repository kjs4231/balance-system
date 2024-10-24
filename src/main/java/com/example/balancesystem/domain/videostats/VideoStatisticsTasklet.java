package com.example.balancesystem.domain.videostats;

import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.video.VideoRepository;
import com.example.balancesystem.domain.videohistory.PlayHistory;
import com.example.balancesystem.domain.videohistory.PlayHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class VideoStatisticsTasklet {

    private final VideoRepository videoRepository;
    private final VideoStatisticsRepository videoStatisticsRepository;
    private final PlayHistoryRepository playHistoryRepository; // PlayHistory Repository 추가

    private final ConcurrentHashMap<String, Boolean> processedVideos = new ConcurrentHashMap<>();

    // 특정 비디오의 총 재생 시간을 계산하는 메서드
    private Long calculateTotalPlayTime(Video video) {
        List<PlayHistory> playHistories = playHistoryRepository.findByVideo(video);
        return playHistories.stream()
                .mapToLong(PlayHistory::getPlayTime)  // 각 플레이 히스토리의 재생 시간을 가져와 합산
                .sum();
    }

    // 일간 통계 생성
    public void processStatisticsForDay(LocalDate date) {
        List<Video> videos = videoRepository.findAll();
        for (Video video : videos) {
            String key = "day_" + video.getVideoId() + "_" + date;
            if (processedVideos.putIfAbsent(key, true) == null) {
                try {
                    Long viewCount = (long) video.getViewCount();  // int -> Long 변환
                    Long totalPlayTime = calculateTotalPlayTime(video); // PlayHistory에서 총 재생 시간 계산

                    VideoStatistics statistics = new VideoStatistics(video, StatType.DAY, date, viewCount, totalPlayTime);
                    videoStatisticsRepository.save(statistics);
                } catch (DataIntegrityViolationException e) {
                    System.out.println("이미 존재하는 통계 데이터입니다: video_id=" + video.getVideoId() + ", date=" + date);
                }
            } else {
                System.out.println("메모리에서 중복이 감지되었습니다: video_id=" + video.getVideoId() + ", date=" + date);
            }
        }
    }

    // 주간 통계 생성
    public void processStatisticsForWeek(LocalDate date) {
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);

        List<Video> videos = videoRepository.findAll();
        for (Video video : videos) {
            String key = "week_" + video.getVideoId() + "_" + startOfWeek;
            if (processedVideos.putIfAbsent(key, true) == null) {
                try {
                    // 주간 통계를 생성하기 위해 일일 통계 데이터를 집계
                    List<VideoStatistics> dailyStatistics = videoStatisticsRepository
                            .findByVideoAndStatTypeAndDateBetween(video, StatType.DAY, startOfWeek, endOfWeek);

                    Long totalViewCount = dailyStatistics.stream()
                            .mapToLong(VideoStatistics::getViewCount)
                            .sum();
                    Long totalPlayTime = dailyStatistics.stream()
                            .mapToLong(VideoStatistics::getTotalPlayTime)
                            .sum();

                    // 주간 통계 생성
                    VideoStatistics statistics = new VideoStatistics(video, StatType.WEEK, startOfWeek, totalViewCount, totalPlayTime);
                    videoStatisticsRepository.save(statistics);
                } catch (DataIntegrityViolationException e) {
                    System.out.println("이미 존재하는 통계 데이터입니다: video_id=" + video.getVideoId() + ", week_start=" + startOfWeek);
                }
            } else {
                System.out.println("메모리에서 중복이 감지되었습니다: video_id=" + video.getVideoId() + ", week_start=" + startOfWeek);
            }
        }
    }

    // 월간 통계 생성
    public void processStatisticsForMonth(LocalDate date) {
        YearMonth month = YearMonth.from(date);
        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        List<Video> videos = videoRepository.findAll();
        for (Video video : videos) {
            String key = "month_" + video.getVideoId() + "_" + month;
            if (processedVideos.putIfAbsent(key, true) == null) {
                try {
                    // 월간 통계를 생성하기 위해 주간 통계 데이터를 집계
                    List<VideoStatistics> weeklyStatistics = videoStatisticsRepository
                            .findByVideoAndStatTypeAndDateBetween(video, StatType.WEEK, startOfMonth, endOfMonth);

                    Long totalViewCount = weeklyStatistics.stream()
                            .mapToLong(VideoStatistics::getViewCount)
                            .sum();
                    Long totalPlayTime = weeklyStatistics.stream()
                            .mapToLong(VideoStatistics::getTotalPlayTime)
                            .sum();

                    // 월간 통계 생성
                    VideoStatistics statistics = new VideoStatistics(video, StatType.MONTH, startOfMonth, totalViewCount, totalPlayTime);
                    videoStatisticsRepository.save(statistics);
                } catch (DataIntegrityViolationException e) {
                    System.out.println("이미 존재하는 통계 데이터입니다: video_id=" + video.getVideoId() + ", month=" + month);
                }
            } else {
                System.out.println("메모리에서 중복이 감지되었습니다: video_id=" + video.getVideoId() + ", month=" + month);
            }
        }
    }

}
