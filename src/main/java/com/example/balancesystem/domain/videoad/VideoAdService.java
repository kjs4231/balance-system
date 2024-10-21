package com.example.balancesystem.domain.videoad;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoAdService {

    private final VideoAdRepository videoAdRepository;

    @Transactional
    public void increaseViewCount(Video video, Ad ad) {
        VideoAd videoAd = videoAdRepository.findByVideoAndAd(video, ad)
                .orElseThrow(() -> new RuntimeException("비디오-광고 관계를 찾을 수 없습니다."));

        // 광고 조회수 증가
        videoAd.increaseViewCount();
        videoAdRepository.save(videoAd);
        System.out.println("광고 조회수 증가 - 광고 ID: " + ad.getAdId() + ", 조회수: " + videoAd.getViewCount());
    }
}
