package com.example.contentservice.ad;

import com.example.contentservice.adhistory.AdHistoryService;
import com.example.contentservice.video.Video;
import com.example.contentservice.videohistory.PlayHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdHistoryService adHistoryService;
    private final AdRepository adRepository;
    private final PlayHistoryService playHistoryService;

    @Transactional
    public void handleAdViews(Video video, Long userId, int currentPlayedAt) {
        if (playHistoryService.isAbusiveAccess(userId, video)) {
            System.out.println("어뷰징으로 인해 광고 시청 횟수가 증가하지 않습니다.");
            return;
        }

        List<Ad> ads = adRepository.findAll();

        if (ads.isEmpty()) {
            throw new RuntimeException("등록된 광고가 없습니다");
        }

        int[] adPlayTimes = {300, 600}; // 광고 등장 시점

        for (int i = 0; i < adPlayTimes.length; i++) {
            int adPlayTime = adPlayTimes[i];

            if (currentPlayedAt >= adPlayTime) {
                Ad ad = ads.get(i % ads.size()); // 순환적으로 광고 선택
                LocalDate viewDate = LocalDate.now();

                // 기존에 해당 광고를 시청했는지 확인
                if (!adHistoryService.hasUserViewedAd(userId, ad, video)) {
                    adHistoryService.saveAdHistory(userId, ad, video, viewDate);

                    video.increaseAdViewCount();
                    System.out.println("광고 시청 기록 저장 완료: 사용자 ID - " + userId + ", 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId());
                } else {
                    System.out.println("중복된 광고 시청 기록이므로 저장 생략: 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId());
                }
            }
        }
    }
}
