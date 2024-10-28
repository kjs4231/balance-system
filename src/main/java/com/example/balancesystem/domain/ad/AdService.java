package com.example.balancesystem.domain.ad;

import com.example.balancesystem.domain.adhistory.AdHistoryService;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdHistoryService adHistoryService;
    private final AdRepository adRepository;

    @Transactional
    public void handleAdViews(Video video, User user, int currentPlayedAt) {
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
                if (!adHistoryService.hasUserViewedAd(user, ad, video)) {
                    adHistoryService.saveAdHistory(user, ad, video, viewDate);
                    System.out.println("광고 시청 기록 저장 완료: 사용자 - " + user.getUsername() + ", 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId());
                } else {
                    System.out.println("중복된 광고 시청 기록이므로 저장 생략: 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId());
                }
            }
        }
    }
}
