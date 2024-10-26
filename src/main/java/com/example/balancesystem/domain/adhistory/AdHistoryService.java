package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdHistoryService {

    private final AdHistoryRepository adHistoryRepository;

    @Transactional(readOnly = true)
    public boolean hasUserViewedAdInVideo(User user, Ad ad, Video video) {
        boolean exists = adHistoryRepository.existsByUserAndAdAndVideoAndViewedTrue(user, ad, video);
        System.out.println("광고 시청 여부 확인: 사용자 - " + user.getUsername() + ", 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId() + ", 시청 여부 - " + exists);
        return exists;
    }

    @Transactional
    public void saveAdHistory(User user, Ad ad, Video video) {
        AdHistory adHistory = new AdHistory(user, ad, video, LocalDateTime.now());
        adHistory.markAsViewed(); // 광고 시청 기록 업데이트
        adHistoryRepository.save(adHistory);
        System.out.println("광고 시청 기록을 저장했습니다: 사용자 - " + user.getUsername() + ", 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId());
    }
}
