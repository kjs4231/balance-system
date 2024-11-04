package com.example.contentservice.ad;

import com.example.contentservice.adhistory.AdHistoryService;
import com.example.contentservice.video.Video;
import com.example.contentservice.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    @Transactional
    public void handleAdViews(Video video, Long userId, int currentPlayedAt) {
        String lockKey = "ad-lock:" + userId + ":" + video.getVideoId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락 획득 시도, 최대 1초 대기 후 실패
            boolean isLockAcquired = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLockAcquired) {
                System.out.println("동시성 제어로 인해 광고 조회수가 증가하지 않습니다.");
                return;
            }

            // 광고 어뷰징 검사
            if (isAdAbusiveAccess(userId, video)) {
                System.out.println("어뷰징으로 인해 광고 시청 횟수가 증가하지 않습니다.");
                return;
            }

            List<Ad> ads = adRepository.findAll();
            if (ads.isEmpty()) {
                throw new RuntimeException("등록된 광고가 없습니다");
            }

            // 광고 등장 시점
            int[] adPlayTimes = {300, 600};

            for (int i = 0; i < adPlayTimes.length; i++) {
                int adPlayTime = adPlayTimes[i];

                // 현재 재생 시간이 광고 등장 시점을 지났을 때만 광고 조회수를 증가
                if (currentPlayedAt >= adPlayTime) {
                    Ad ad = ads.get(i % ads.size());
                    LocalDate viewDate = LocalDate.now();

                    // 광고 시청 기록 저장
                    adHistoryService.saveAdHistoryIfNotExists(userId, ad, video, viewDate);

                    // 광고 조회수를 Redis에 저장
                    String adViewCountKey = "video:adViewCount:" + video.getVideoId();
                    redisTemplate.opsForValue().increment(adViewCountKey);

                    System.out.println("광고 시청 기록 저장 완료: 사용자 ID - " + userId + ", 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 획득 중 인터럽트가 발생했습니다.", e);
        } finally {
            // 락 해제
            lock.unlock();
        }
    }

    private boolean isAdAbusiveAccess(Long userId, Video video) {
        String redisKey = "ad_viewing:" + userId + ":" + video.getVideoId();

        // 30초 내 중복 시청 방지
        if (redisTemplate.hasKey(redisKey)) {
            System.out.println("30초 내 중복된 광고 시청 요청입니다. 광고 조회수는 카운트되지 않습니다.");
            return true;
        }

        // TTL 중복 방지
        redisTemplate.opsForValue().set(redisKey, "viewing", 30, TimeUnit.SECONDS);
        return false;
    }
}
