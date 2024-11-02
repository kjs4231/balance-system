package com.example.batchservice.videostats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoStatisticsService {

    private final VideoStatisticsRepository videoStatisticsRepository;

    public Map<String, List<String>> getTop5ByViewCount(LocalDate date) {
        Map<String, List<String>> result = new HashMap<>();
        result.put("day", getTop5ViewCountByPeriod(date, date));
        result.put("week", getTop5ViewCountByPeriod(date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY)));
        result.put("month", getTop5ViewCountByPeriod(date.withDayOfMonth(1), YearMonth.from(date).atEndOfMonth()));
        return result;
    }

    public Map<String, List<String>> getTop5ByPlayTime(LocalDate date) {
        Map<String, List<String>> result = new HashMap<>();
        result.put("day", getTop5PlayTimeByPeriod(date, date));
        result.put("week", getTop5PlayTimeByPeriod(date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY)));
        result.put("month", getTop5PlayTimeByPeriod(date.withDayOfMonth(1), YearMonth.from(date).atEndOfMonth()));
        return result;
    }

    private List<String> getTop5ViewCountByPeriod(LocalDate startDate, LocalDate endDate) {
        return videoStatisticsRepository.findTop5ByDateBetweenOrderByViewCountDesc(startDate, endDate)
                .stream()
                .map(stat -> "Video ID: " + stat.getVideoId() + ", View Count: " + stat.getViewCount())
                .collect(Collectors.toList());
    }

    private List<String> getTop5PlayTimeByPeriod(LocalDate startDate, LocalDate endDate) {
        return videoStatisticsRepository.findTop5ByDateBetweenOrderByTotalPlayTimeDesc(startDate, endDate)
                .stream()
                .map(stat -> "Video ID: " + stat.getVideoId() + ", Play Time: " + stat.getTotalPlayTime())
                .collect(Collectors.toList());
    }
}
