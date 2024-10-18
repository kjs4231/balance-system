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
            boolean adAlreadyViewed = adHistoryService.hasUserViewedAd(user, ad);

            if (currentPlayedAt >= ad.getTriggerTime() && !adAlreadyViewed) {
                ad.increaseViewCount();
                ad.markAsViewed();
                adRepository.save(ad);

                adHistoryService.saveAdHistory(user, ad);
            }
        });
    }
}
