package com.example.contentservice.ad;

import com.example.contentservice.adhistory.AdHistoryService;
import com.example.contentservice.video.Video;
import com.example.contentservice.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdHistoryService adHistoryService;
    private final AdRepository adRepository;
    private final VideoRepository videoRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void handleAdViews(Video video, Long userId, int currentPlayedAt) {
        String redisKey = "ad_viewing:" + userId + ":" + video.getVideoId();
        Boolean isNewAccess = redisTemplate.opsForValue().setIfAbsent(redisKey, "viewing", 30, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(isNewAccess)) {
            System.out.println("어뷰징으로 인해 광고 시청 횟수가 증가하지 않습니다.");
            return;
        }

        List<Ad> ads = adRepository.findAll();
        if (ads.isEmpty()) {
            throw new RuntimeException("등록된 광고가 없습니다");
        }

        // 광고 등장 시점 정의
        int[] adPlayTimes = {300, 600};

        for (int i = 0; i < adPlayTimes.length; i++) {
            int adPlayTime = adPlayTimes[i];

            // 현재 재생 시간이 광고 등장 시점을 지났을 때만 광고 조회수를 증가
            if (currentPlayedAt >= adPlayTime) {
                Ad ad = ads.get(i % ads.size());
                LocalDate viewDate = LocalDate.now();

                // 광고 시청 기록 저장 (중복 방지 포함)
                adHistoryService.saveAdHistoryIfNotExists(userId, ad, video, viewDate);

                // 광고 조회수를 Redis에 임시 저장
                String adViewCountKey = "video:adViewCount:" + video.getVideoId();
                redisTemplate.opsForValue().increment(adViewCountKey);

                System.out.println("광고 시청 기록 저장 완료: 사용자 ID - " + userId + ", 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId());
            }
        }
    }

}
