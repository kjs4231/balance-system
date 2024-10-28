package com.example.balancesystem.domain.videorevenue;

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
public class VideoRevenueService {

    private final VideoRevenueRepository videoRevenueRepository;

    public Map<String, List<VideoRevenueDto>> getRevenuesByPeriod(LocalDate date) {
        Map<String, List<VideoRevenueDto>> result = new HashMap<>();

        result.put("day", getRevenueByPeriod(date, date));
        result.put("week", getRevenueByPeriod(date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY)));
        result.put("month", getRevenueByPeriod(date.withDayOfMonth(1), YearMonth.from(date).atEndOfMonth()));

        return result;
    }

    private List<VideoRevenueDto> getRevenueByPeriod(LocalDate startDate, LocalDate endDate) {
        return videoRevenueRepository.findByDateBetween(startDate, endDate).stream()
                .map(revenue -> new VideoRevenueDto(
                        revenue.getRevenueId(),
                        revenue.getVideo().getTitle(),
                        revenue.getViewRevenue(),
                        revenue.getAdRevenue(),
                        revenue.getTotalRevenue(),
                        revenue.getDate()
                ))
                .collect(Collectors.toList());
    }
}
