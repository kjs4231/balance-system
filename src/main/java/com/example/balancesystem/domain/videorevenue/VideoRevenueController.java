package com.example.balancesystem.domain.videorevenue;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/revenues")
public class VideoRevenueController {

    private final VideoRevenueService videoRevenueService;

    @GetMapping
    public List<VideoRevenueDto> getRevenuesByPeriod(@RequestParam String period,
                                                     @RequestParam(required = false) String date) {
        // 날짜가 없거나 현재 날짜와 같은 경우, 기본값으로 어제 날짜 사용
        LocalDate queryDate = (date == null || LocalDate.parse(date).isEqual(LocalDate.now()))
                ? LocalDate.now().minusDays(1)
                : LocalDate.parse(date);

        // 해당 날짜 기준으로 일, 주, 월 정산 데이터 가져오기
        Map<String, List<VideoRevenueDto>> revenuesByPeriod = videoRevenueService.getRevenuesByPeriod(queryDate);

        return revenuesByPeriod.getOrDefault(period, List.of());
    }
}
