package com.example.balancesystem.domain.ad;

import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.videoad.VideoAd;
import com.example.balancesystem.domain.videoad.VideoAdRepository;
import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.adhistory.AdHistoryService;
import com.example.balancesystem.domain.videoad.VideoAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final VideoAdRepository videoAdRepository;
    private final AdHistoryService adHistoryService;
    private final VideoAdService videoAdService; // VideoAdService 추가

    @Transactional
    public void handleAdViews(Video video, User user, int currentPlayedAt) {

        List<VideoAd> videoAds = videoAdRepository.findAllByVideo(video);

        for (VideoAd videoAd : videoAds) {
            Ad ad = videoAd.getAd();


            int adPlayTime = 0;

            if (videoAds.indexOf(videoAd) == 0) {
                adPlayTime = 300; // 첫 번째 광고의 등장 시점
            } else if (videoAds.indexOf(videoAd) == 1) {
                adPlayTime = 600; // 두 번째 광고의 등장 시점
            } else {
                // 고정된 300초, 600초 이상의 광고는 없으므로 반복 종료
                break;
            }

            // 현재 시청 시간이 광고 등장 시점 이상일 때만 시청 처리
            if (currentPlayedAt >= adPlayTime) {
                boolean hasViewed = adHistoryService.hasUserViewedAd(user, ad);

                if (!hasViewed) {
                    // 광고를 시청한 적이 없다면 광고 시청 기록을 저장
                    adHistoryService.saveAdHistory(user, ad);
                    System.out.println("광고 시청 기록 저장 완료: 사용자 - " + user.getUsername() + ", 광고 ID - " + ad.getAdId());

                    // 광고 조회수 증가 처리
                    videoAdService.increaseViewCount(video, ad); // 조회수 증가 로직 추가
                }
            }
        }
    }
}
