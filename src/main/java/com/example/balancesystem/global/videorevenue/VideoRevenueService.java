package com.example.balancesystem.global.videorevenue;

import com.example.balancesystem.global.videorevenue.dsl.VideoRevenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        result.put("day", getRevenueByPeriod(date, date, false));
        result.put("week", getRevenueByPeriod(date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY), true));
        result.put("month", getRevenueByPeriod(date.withDayOfMonth(1), YearMonth.from(date).atEndOfMonth(), true));

        return result;
    }

    private List<VideoRevenueDto> getRevenueByPeriod(LocalDate startDate, LocalDate endDate, boolean isAggregate) {
        List<VideoRevenue> revenues = videoRevenueRepository.findByDateBetween(startDate, endDate);

        if (isAggregate) {
            BigDecimal totalViewRevenue = revenues.stream()
                    .map(VideoRevenue::getViewRevenue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalAdRevenue = revenues.stream()
                    .map(VideoRevenue::getAdRevenue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalRevenue = revenues.stream()
                    .map(VideoRevenue::getTotalRevenue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return List.of(new VideoRevenueDto(
                    null,
                    "Total Revenue for Period",
                    totalViewRevenue,
                    totalAdRevenue,
                    totalRevenue,
                    endDate
            ));
        } else {
            return revenues.stream()
                    .map(revenue -> new VideoRevenueDto(
                            revenue.getRevenueId(),
                            "Video ID: " + revenue.getVideoId(),
                            revenue.getViewRevenue(),
                            revenue.getAdRevenue(),
                            revenue.getTotalRevenue(),
                            revenue.getDate()
                    ))
                    .collect(Collectors.toList());
        }
    }
}
