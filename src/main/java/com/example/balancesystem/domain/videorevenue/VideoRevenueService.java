package com.example.balancesystem.domain.videorevenue;

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

        // 일간 조회는 개별 데이터를 그대로 반환
        result.put("day", getRevenueByPeriod(date, date, false));

        // 주간 및 월간 조회는 합산된 금액을 반환
        result.put("week", getRevenueByPeriod(date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY), true));
        result.put("month", getRevenueByPeriod(date.withDayOfMonth(1), YearMonth.from(date).atEndOfMonth(), true));

        return result;
    }

    private List<VideoRevenueDto> getRevenueByPeriod(LocalDate startDate, LocalDate endDate, boolean isAggregate) {
        List<VideoRevenue> revenues = videoRevenueRepository.findByDateBetween(startDate, endDate);

        if (isAggregate) {
            // 주간/월간 조회 시 합산된 금액을 계산
            BigDecimal totalViewRevenue = revenues.stream()
                    .map(VideoRevenue::getViewRevenue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalAdRevenue = revenues.stream()
                    .map(VideoRevenue::getAdRevenue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalRevenue = revenues.stream()
                    .map(VideoRevenue::getTotalRevenue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 합산된 결과를 하나의 VideoRevenueDto로 반환
            return List.of(new VideoRevenueDto(
                    null, // 개별 revenueId는 필요 없음
                    "Total Revenue for Period", // 합산 결과임을 나타내는 이름
                    totalViewRevenue,
                    totalAdRevenue,
                    totalRevenue,
                    endDate // 조회 마지막 날짜 표시
            ));
        } else {
            // 일간 조회의 경우 개별 데이터를 그대로 반환
            return revenues.stream()
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
}
