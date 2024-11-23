package com.example.balancesystem.domain.content.video;

import com.example.balancesystem.domain.content.video.dsl.VideoRepository;
import com.example.balancesystem.domain.content.playhistory.PlayHistoryService;
import com.example.balancesystem.domain.content.ad.AdService;
import com.example.balancesystem.domain.content.playhistory.PlayHistory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@EnableAsync
public class VideoService {

    private final VideoRepository videoRepository;
    private final PlayHistoryService playHistoryService;
    private final AdService adService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> luaScript;

    /**
     * 동영상 저장
     */
    @Transactional
    public Video saveVideo(VideoDto videoDto) {
        Long ownerId = videoDto.getOwnerId();
        Video video = new Video(videoDto.getTitle(), videoDto.getDuration(), ownerId);
        return videoRepository.save(video);
    }

    /**
     * 동영상 재생
     */
    @Transactional
    public String playVideo(Long userId, Long videoId, HttpServletRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));

        PlayHistory playHistory = playHistoryService.handlePlay(userId, video, request);

        adService.handleAdViews(video, userId, playHistory.getLastPlayedAt(), request);

        int lastPlayedAt = playHistory.getLastPlayedAt();
        String hashKey = "video:stats:" + videoId; // Redis Hash Key
        String ttlField = "ttl:" + userId; // TTL 필드 이름
        String countField = "view_count"; // 조회수 필드 이름

        // Lua 스크립트 실행
        Long result = redisTemplate.execute(
                luaScript,
                Arrays.asList(hashKey),
                ttlField, countField, "30", "1"
        );

        if (result == -1) {
            return "게시자가 자신의 동영상을 시청했습니다.";
        } else if (result == -2) {
            return "중복 요청으로 조회수가 증가하지 않습니다.";
        }

        return lastPlayedAt == 0 || lastPlayedAt >= video.getDuration()
                ? "동영상을 처음부터 재생합니다."
                : "동영상을 " + lastPlayedAt + "초부터 이어서 재생합니다.";
    }

    /**
     * 동영상 재생 중지
     */
    @Transactional
    public void pauseVideo(Long userId, Long videoId, int currentPlayedAt, HttpServletRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));

        // PlayHistory 업데이트
        playHistoryService.handlePause(userId, video, currentPlayedAt);

        // 광고 조회수 업데이트
        adService.handleAdViews(video, userId, currentPlayedAt, request);
    }

    /**
     * Redis의 조회수를 MySQL로 동기화
     */
    @Async
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncViewCountsToDatabase() {
        System.out.println("조회수 동기화 시작");

        Set<String> videoKeys = redisTemplate.keys("video:stats:*");
        Map<Long, Integer> viewCounts = new HashMap<>();

        if (videoKeys != null) {
            videoKeys.forEach(key -> {
                String videoIdStr = key.split(":")[2];
                Long videoId = Long.parseLong(videoIdStr);
                String viewCountStr = (String) redisTemplate.opsForHash().get(key, "view_count");

                if (viewCountStr != null) {
                    int viewCount = Integer.parseInt(viewCountStr);
                    viewCounts.put(videoId, viewCount);
                    redisTemplate.delete(key); // Redis 데이터 삭제
                }
            });

            syncViewCountsBatch(viewCounts); // 배치 동기화
        }
    }

    /**
     * MySQL에 조회수 배치 업데이트
     */
    @Transactional
    public void syncViewCountsBatch(Map<Long, Integer> viewCounts) {
        viewCounts.forEach((videoId, count) -> {
            videoRepository.findById(videoId).ifPresent(video -> {
                video.increaseViewCountBy(count);
                videoRepository.save(video);
                System.out.println("동기화 완료: 비디오 ID - " + videoId + ", 조회수: " + count);
            });
        });
    }

    /**
     * 사용자 IP 가져오기
     */
    private String getUserIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
                ? request.getRemoteAddr() : ip;
    }
}