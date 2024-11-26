package com.example.balancesystem.domain.content.ad;

import com.example.balancesystem.domain.content.ad.dsl.AdRepository;
import com.example.balancesystem.domain.content.video.Video;
import com.example.balancesystem.domain.content.adhistory.AdHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdHistoryService adHistoryService;
    private final AdRepository adRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> luaScript;

    @Transactional
    public void handleAdViews(Video video, Long userId, int currentPlayedAt, HttpServletRequest request) {
        String ip = getUserIp(request);

        if (isAdAbusiveAccess(userId, video, ip)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "어뷰징으로 인해 조회수가 증가하지 않습니다.");
        }

        int[] adPlayTimes = {300, 600};
        List<Ad> ads = adRepository.findAll();

        if (ads.isEmpty()) {
            throw new RuntimeException("등록된 광고가 없습니다");
        }

        for (int i = 0; i < adPlayTimes.length; i++) {
            if (currentPlayedAt >= adPlayTimes[i]) {
                Ad ad = ads.get(i % ads.size());
                LocalDate viewDate = LocalDate.now();

                adHistoryService.saveAdHistoryIfNotExists(userId, ad, video, viewDate);

                String adViewCountKey = "video:adViewCount:" + video.getVideoId();
                String ttlKey = "ad_viewing:" + userId + ":" + video.getVideoId() + ":" + ip;

                Long result = redisTemplate.execute(
                        luaScript,
                        Arrays.asList(adViewCountKey, ttlKey),
                        String.valueOf(userId), String.valueOf(video.getOwnerId()), "30", "1"
                );

                if (result == -1) {
                    System.out.println("게시자가 자신의 동영상을 재생한 경우: 조회수 증가 안 함");
                } else if (result == -2) {
                    System.out.println("중복 재생으로 간주: 조회수 증가 안 함");
                } else {
                    System.out.println("광고 조회수 증가 성공: 현재 조회수 - " + result);
                }

                System.out.println("광고 시청 기록 저장 완료: 사용자 ID - " + userId + ", 광고 ID - " + ad.getAdId() + ", 영상 ID - " + video.getVideoId());
            }
        }
    }

    public boolean isAdAbusiveAccess(Long userId, Video video, String ip) {
        if (userId.equals(video.getOwnerId())) {
            System.out.println("게시자는 자신의 동영상을 시청해도 광고 시청 횟수가 증가하지 않습니다.");
            return true;
        }

        String redisKey = "ad_viewing:" + userId + ":" + video.getVideoId() + ":" + ip;
        if (redisTemplate.hasKey(redisKey)) {
            System.out.println("30초 내 중복된 광고 시청 요청입니다. 광고 조회수는 카운트되지 않습니다.");
            return true;
        }

        return false;
    }

    private String getUserIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
