package com.example.contentservice.videohistory;

import com.example.contentservice.video.Video;
import com.example.contentservice.videohistory.dsl.PlayHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayHistoryService {

    private final PlayHistoryRepository playHistoryRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> luaScript;

    @Transactional
    public PlayHistory handlePlay(Long userId, Video video, HttpServletRequest request) {
        // 요청으로부터 IP 주소 가져오기
        String ip = getUserIp(request);

        // 어뷰징 방지 로직 확인
        if (isAbusiveAccess(userId, video, ip)) {
            throw new IllegalStateException("어뷰징으로 인해 조회수가 증가하지 않습니다.");
        }

        // 조회수와 TTL 키 생성 (IP 포함)
        String viewCountKey = "video:viewCount:" + video.getVideoId();
        String ttlKey = "viewing:" + userId + ":" + video.getVideoId() + ":" + ip;

        // Lua 스크립트 실행
        Long result = redisTemplate.execute(
                luaScript,
                Arrays.asList(viewCountKey, ttlKey),
                String.valueOf(userId), String.valueOf(video.getOwnerId()), "30", "1"
        );
        System.out.println("Lua 스크립트 실행 결과: " + result);

        // 결과 해석
        if (result == -1) {
            System.out.println("게시자가 자신의 동영상을 재생한 경우: 조회수 증가 안 함");
        } else if (result == -2) {
            System.out.println("중복 재생으로 간주: 조회수 증가 안 함");
        } else {
            System.out.println("조회수 증가 성공: 현재 조회수 - " + result);
        }

        // 시청 기록 저장
        Optional<PlayHistory> optionalPlayHistory = playHistoryRepository.findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(userId, video);
        int startFrom = optionalPlayHistory.map(PlayHistory::getLastPlayedAt).orElse(0);

        PlayHistory newPlayHistory = new PlayHistory(userId, video, LocalDateTime.now(), startFrom);
        newPlayHistory.setPlayTime(0);
        newPlayHistory.setLastPlayedAt(startFrom);
        playHistoryRepository.save(newPlayHistory);

        return newPlayHistory;
    }

    // 일시 정지 시 기록 갱신
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

    // 어뷰징 여부 확인 (IP와 인증키 기반)
    public boolean isAbusiveAccess(Long userId, Video video, String ip) {
        if (userId.equals(video.getOwnerId())) {
            System.out.println("게시자는 자신의 동영상을 시청해도 조회수와 광고 시청 횟수가 증가하지 않습니다.");
            return true;
        }

        // 중복 요청 여부 확인 (IP 포함)
        String redisKey = "viewing:" + userId + ":" + video.getVideoId() + ":" + ip;
        if (redisTemplate.hasKey(redisKey)) {
            System.out.println("30초 내 중복된 요청입니다. 조회수는 카운트되지 않습니다.");
            return true;
        }

        return false;
    }

    // 클라이언트 IP 가져오기
    private String getUserIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
