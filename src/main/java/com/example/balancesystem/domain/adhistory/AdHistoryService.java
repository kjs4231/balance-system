package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdHistoryService {

    private final AdHistoryRepository adHistoryRepository;

    @Transactional(readOnly = true)
    public boolean hasUserViewedAd(User user, Ad ad) {
        boolean exists = adHistoryRepository.existsByUserAndAdAndViewedTrue(user, ad);
        System.out.println("광고 시청 여부 확인: 사용자 - " + user.getUsername() + ", 광고 ID - " + ad.getAdId() + ", 시청 여부 - " + exists);
        return exists;
    }

    @Transactional
    public void saveAdHistory(User user, Ad ad) {
        AdHistory adHistory = adHistoryRepository.findByUserAndAd(user, ad)
                .orElseGet(() -> new AdHistory(user, ad, LocalDateTime.now()));
        adHistory.markAsViewed(); // 광고 시청 기록 업데이트
        adHistoryRepository.save(adHistory);
        System.out.println("광고 시청 기록을 저장했습니다: 사용자 - " + user.getUsername() + ", 광고 ID - " + ad.getAdId());
    }
}