package com.example.balancesystem.domain.videoad;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoAdService {

    private final VideoAdRepository videoAdRepository;

    @Transactional
    public void increaseViewCount(Video video, Ad ad) {
        List<VideoAd> videoAds = videoAdRepository.findByVideoAndAd(video, ad);
        if (videoAds.isEmpty()) {
            throw new RuntimeException("비디오-광고 관계를 찾을 수 없습니다.");
        }

        // 모든 결과에 대해 조회수를 증가시키거나 첫 번째 결과에 대해서만 증가
        for (VideoAd videoAd : videoAds) {
            videoAd.increaseViewCount();
            videoAdRepository.save(videoAd);
            System.out.println("광고 조회수 증가 - 광고 ID: " + ad.getAdId() + ", 조회수: " + videoAd.getViewCount());
        }
    }

}
