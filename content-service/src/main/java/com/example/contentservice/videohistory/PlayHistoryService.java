package com.example.contentservice.videohistory;

import com.example.contentservice.video.Video;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PlayHistoryService {

    private final PlayHistoryRepository playHistoryRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    @Transactional
    public PlayHistory handlePlay(Long userId, Video video) {
        String lockKey = "play-lock:" + userId + ":" + video.getVideoId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락 획득 시도, 최대 1초 대기 후 실패
            boolean isLockAcquired = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLockAcquired) {
                System.out.println("동시성 제어로 인해 조회수가 증가하지 않습니다.");
                throw new IllegalStateException("동시성 제어로 인해 조회수가 증가하지 않습니다.");
            }

            // 어뷰징 여부 검사
            if (isAbusiveAccess(userId, video)) {
                throw new IllegalStateException("어뷰징으로 인해 조회수가 증가하지 않습니다.");
            }

            Optional<PlayHistory> optionalPlayHistory = playHistoryRepository.findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(userId, video);

            int startFrom = 0;
            if (optionalPlayHistory.isPresent()) {
                PlayHistory previousPlayHistory = optionalPlayHistory.get();
                startFrom = previousPlayHistory.getLastPlayedAt();
                previousPlayHistory.setCompleted(true);
                playHistoryRepository.save(previousPlayHistory);
            }

            PlayHistory newPlayHistory = new PlayHistory(userId, video, LocalDateTime.now(), startFrom);
            newPlayHistory.setPlayTime(0);
            newPlayHistory.setLastPlayedAt(startFrom);
            playHistoryRepository.save(newPlayHistory);

            // 조회수를 Redis에 저장
            String viewCountKey = "video:viewCount:" + video.getVideoId();
            redisTemplate.opsForValue().increment(viewCountKey);

            return newPlayHistory;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 획득 중 인터럽트가 발생했습니다.", e);
        } finally {
            // 락 해제
            lock.unlock();
        }
    }

    public boolean isAbusiveAccess(Long userId, Video video) {

        if (userId.equals(video.getOwnerId())) {
            System.out.println("게시자는 자신의 동영상을 시청해도 조회수와 광고 시청 횟수가 증가하지 않습니다.");
            return true;
        }

        // 30초 내 중복 시청 방지
        String redisKey = "viewing:" + userId + ":" + video.getVideoId();
        if (redisTemplate.hasKey(redisKey)) {
            System.out.println("30초 내 중복된 요청입니다. 조회수는 카운트되지 않습니다.");
            return true;
        }

        // TTL 중복 방지
        redisTemplate.opsForValue().set(redisKey, "viewing", 30, TimeUnit.SECONDS);
        return false;
    }

    @Transactional
    public void handlePause(Long userId, Video video, int currentPlayedAt) {
        PlayHistory playHistory = playHistoryRepository.findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(userId, video)
                .orElseThrow(() -> new RuntimeException("시청 기록이 없습니다."));

        int purePlayTime = currentPlayedAt - playHistory.getLastPlayedAt();
        playHistory.setPlayTime(purePlayTime);
        playHistory.setLastPlayedAt(currentPlayedAt);

        if (currentPlayedAt >= video.getDuration()) {
            playHistory.setCompleted(true);
        }

        playHistoryRepository.save(playHistory);
    }

}
