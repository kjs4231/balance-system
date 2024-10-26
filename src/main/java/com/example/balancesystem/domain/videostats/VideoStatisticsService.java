package com.example.balancesystem.domain.videostats;

import com.example.balancesystem.domain.videostats.StatType;
import com.example.balancesystem.domain.videostats.VideoStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoStatisticsService {

    private final VideoStatisticsRepository videoStatisticsRepository;

    public List<String> getTop5VideosByViewCount(StatType statType, LocalDate date) {
        return videoStatisticsRepository.findTop5ByStatTypeAndDateOrderByViewCountDesc(statType, date)
                .stream()
                .map(stat -> "Video ID: " + stat.getVideo().getVideoId() + ", View Count: " + stat.getViewCount())
                .collect(Collectors.toList());
    }

    public List<String> getTop5VideosByPlayTime(StatType statType, LocalDate date) {
        return videoStatisticsRepository.findTop5ByStatTypeAndDateOrderByTotalPlayTimeDesc(statType, date)
                .stream()
                .map(stat -> "Video ID: " + stat.getVideo().getVideoId() + ", Play Time: " + stat.getTotalPlayTime())
                .collect(Collectors.toList());
    }
}
