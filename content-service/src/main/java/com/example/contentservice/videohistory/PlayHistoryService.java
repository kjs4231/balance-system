package com.example.contentservice.videohistory;

import com.example.contentservice.video.Video;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public PlayHistory handlePlay(Long userId, Video video) {
        String redisKey = "viewing:" + userId + ":" + video.getVideoId();
        Boolean isNewAccess = redisTemplate.opsForValue().setIfAbsent(redisKey, "viewing", 30, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(isNewAccess)) {
            System.out.println("어뷰징으로 인해 조회수가 증가하지 않습니다.");
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

        // 조회수를 Redis에 저장 (데이터베이스에는 주기적으로 동기화)
        String viewCountKey = "video:viewCount:" + video.getVideoId();
        redisTemplate.opsForValue().increment(viewCountKey);

        return newPlayHistory;
    }


    public boolean isAbusiveAccess(Long userId, Video video) {
        // 동영상 게시자가 시청하는 경우
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

        // Redis에 TTL 설정을 통해 중복 요청 방지
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

    @Transactional
    public void markCompleted(Long userId, Video video) {
        PlayHistory playHistory = playHistoryRepository.findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(userId, video)
                .orElseGet(() -> {
                    PlayHistory newHistory = new PlayHistory(userId, video, LocalDateTime.now(), 0);
                    newHistory.setCompleted(true);
                    playHistoryRepository.save(newHistory);
                    return newHistory;
                });

        playHistory.setCompleted(true);
        playHistory.setLastPlayedAt(0);
        playHistoryRepository.save(playHistory);
    }

    @Transactional
    public void savePlayHistory(PlayHistory playHistory) {
        playHistoryRepository.save(playHistory);
    }
}
