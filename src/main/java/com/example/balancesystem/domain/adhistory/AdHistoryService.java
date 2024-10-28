package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdHistoryService {

    private final AdHistoryRepository adHistoryRepository;

    @Transactional(readOnly = true)
    public boolean hasUserViewedAd(User user, Ad ad, Video video) {
        LocalDate today = LocalDate.now();
        return adHistoryRepository.existsByUserAndAdAndVideoAndViewDate(user, ad, video, today);
    }

    @Transactional
    public void saveAdHistory(User user, Ad ad, Video video, LocalDate viewDate) {
        if (!hasUserViewedAd(user, ad, video)) {
            AdHistory adHistory = new AdHistory(user, ad, video, viewDate);
            adHistoryRepository.save(adHistory);
            System.out.println("광고 시청 기록이 저장되었습니다: 사용자 - " + user.getUsername() + ", 광고 ID - " + ad.getAdId());
        } else {
            System.out.println("중복된 광고 시청 기록으로 인해 저장이 생략되었습니다.");
        }
    }
}
