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
        LocalDate queryDate = (date == null || LocalDate.parse(date).equals(LocalDate.now()))
                ? LocalDate.now().minusDays(1)
                : LocalDate.parse(date);
        Map<String, List<VideoRevenueDto>> revenuesByPeriod = videoRevenueService.getRevenuesByPeriod(queryDate);
        return revenuesByPeriod.getOrDefault(period, List.of());
    }
}
