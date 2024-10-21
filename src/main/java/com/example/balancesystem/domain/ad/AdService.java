package com.example.balancesystem.domain.ad;

import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.videoad.VideoAd;
import com.example.balancesystem.domain.adhistory.AdHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {

    private final AdHistoryService adHistoryService;

    @Transactional
    public void handleAdViews(Video video, User user, int currentPlayedAt) {

        // 동영상 게시자가 본다면 카운트하지 않음
        if (video.getOwner().getUsername().equals(user.getUsername())) {
            log.info("동영상 게시자가 본 영상입니다. 광고 시청을 카운트하지 않습니다: 사용자 - {}", user.getUsername());
            return;  // 게시자는 조회수나 광고 시청 카운트 안함
        }

        // 광고 시청 처리
        for (VideoAd videoAd : video.getVideoAds()) {
            Ad ad = videoAd.getAd();
            int viewCount = videoAd.getViewCount() != null ? videoAd.getViewCount().intValue() : 0;

            // 광고 트리거 시점 계산 (5분마다 광고 표시)
            int adTriggerTime = (viewCount + 1) * 300;

            // 광고 트리거 타임이 현재 재생 시간에 도달하면 처리
            if (currentPlayedAt >= adTriggerTime) {
                boolean adAlreadyViewed = adHistoryService.hasUserViewedAd(user, ad);

                if (!adAlreadyViewed) {
                    // 광고 시청 기록 저장
                    adHistoryService.saveAdHistory(user, ad);
                    videoAd.increaseViewCount();
                    log.info("광고 시청 기록을 저장합니다: 사용자 - {}, 광고 ID - {}, 현재 재생 위치 - {}", user.getUsername(), ad.getAdId(), currentPlayedAt);
                } else {
                    log.info("광고가 이미 시청됨: 사용자 - {}, 광고 ID - {}", user.getUsername(), ad.getAdId());
                }
            } else {
                log.info("아직 광고 트리거 시간에 도달하지 않음 - 현재 위치: {}, 광고 트리거 시간: {}", currentPlayedAt, adTriggerTime);
            }
        }
    }
}