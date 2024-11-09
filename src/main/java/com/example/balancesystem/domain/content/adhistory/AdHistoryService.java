package com.example.balancesystem.domain.content.adhistory;

import com.example.balancesystem.domain.content.ad.Ad;
import com.example.balancesystem.domain.content.video.Video;
import com.example.balancesystem.domain.content.adhistory.dsl.AdHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdHistoryService {

    private final AdHistoryRepository adHistoryRepository;

    @Transactional
    public void saveAdHistoryIfNotExists(Long userId, Ad ad, Video video, LocalDate viewDate) {
        if (!adHistoryRepository.existsByUserIdAndAdAndVideoAndViewDate(userId, ad, video, viewDate)) {
            AdHistory adHistory = new AdHistory(userId, ad, video, viewDate);
            adHistoryRepository.save(adHistory);
            System.out.println("광고 시청 기록이 저장되었습니다: 사용자 ID - " + userId + ", 광고 ID - " + ad.getAdId());
        } else {
            System.out.println("중복된 광고 시청 기록으로 인해 저장이 생략되었습니다.");
        }
    }
}