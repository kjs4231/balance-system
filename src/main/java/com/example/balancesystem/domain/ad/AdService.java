package com.example.balancesystem.domain.ad;

import com.example.balancesystem.domain.adhistory.AdHistoryService;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final AdHistoryService adHistoryService;

    @Transactional
    public void handleAdViews(Video video, User user, int currentPlayedAt) {
        video.getAds().forEach(ad -> {
            if (currentPlayedAt >= ad.getTriggerTime()) {
                boolean adAlreadyViewed = adHistoryService.hasUserViewedAd(user, ad);

                if (!adAlreadyViewed) {
                    // 광고 시청 횟수 증가 및 시청 완료 처리
                    ad.increaseViewCount();
                    adRepository.save(ad);

                    // 광고 시청 이력 저장
                    adHistoryService.saveAdHistory(user, ad);
                }
            }
        });
    }
}
