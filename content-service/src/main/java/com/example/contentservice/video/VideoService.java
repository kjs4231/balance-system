package com.example.contentservice.video;

import com.example.contentservice.ad.AdService;
import com.example.contentservice.videohistory.PlayHistory;
import com.example.contentservice.videohistory.PlayHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class VideoService {

    private final VideoRepository videoRepository;
    private final PlayHistoryService playHistoryService;
    private final AdService adService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Video saveVideo(VideoDto videoDto) {
        Long ownerId = videoDto.getOwnerId();
        Video video = new Video(videoDto.getTitle(), videoDto.getDuration(), ownerId);
        return videoRepository.save(video);
    }

    @Transactional
    public String playVideo(Long userId, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));

        PlayHistory playHistory = playHistoryService.handlePlay(userId, video);

        // 광고 재생 처리
        handleAdPlayback(video, userId, playHistory.getLastPlayedAt());

        int lastPlayedAt = playHistory.getLastPlayedAt();

        return lastPlayedAt == 0 || lastPlayedAt >= video.getDuration()
                ? "동영상을 처음부터 재생합니다."
                : "동영상을 " + lastPlayedAt + "초부터 이어서 재생합니다.";
    }

    @Transactional
    public void pauseVideo(Long userId, Long videoId, int currentPlayedAt) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));

        playHistoryService.handlePause(userId, video, currentPlayedAt);

        // 광고 재생 처리
        handleAdPlayback(video, userId, currentPlayedAt);
    }

    // 광고 재생 로직을 AdService로 위임
    private void handleAdPlayback(Video video, Long userId, int currentPlayedAt) {
        adService.handleAdViews(video, userId, currentPlayedAt);
    }

    @Scheduled(fixedRate = 60000)
    public void syncViewCountsToDatabase() {
        // 비디오 조회수 동기화
        Set<String> viewKeys = redisTemplate.keys("video:viewCount:*");
        if (viewKeys != null) {
            for (String key : viewKeys) {
                String[] keyParts = key.split(":");
                if (keyParts.length == 3) {
                    String videoIdStr = keyParts[2];
                    try {
                        Long videoId = Long.parseLong(videoIdStr);
                        String countStr = redisTemplate.opsForValue().get(key);
                        if (countStr != null) {
                            int count = Integer.parseInt(countStr);
                            videoRepository.findById(videoId).ifPresent(video -> {
                                video.increaseViewCountBy(count);
                                videoRepository.save(video);
                                System.out.println("조회수 데이터베이스 업데이트 완료: 영상 ID - " + videoId + ", 증가한 조회수 - " + count);
                            });
                            redisTemplate.delete(key);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 키 형식으로 인해 조회수 업데이트를 건너뜁니다: " + key);
                    }
                }
            }
        }

        // 광고 조회수 동기화
        Set<String> adKeys = redisTemplate.keys("video:adViewCount:*");
        if (adKeys != null) {
            for (String key : adKeys) {
                String[] keyParts = key.split(":");
                if (keyParts.length == 3) {
                    String videoIdStr = keyParts[2];
                    try {
                        Long videoId = Long.parseLong(videoIdStr);
                        String countStr = redisTemplate.opsForValue().get(key);
                        if (countStr != null) {
                            int count = Integer.parseInt(countStr);
                            videoRepository.findById(videoId).ifPresent(video -> {
                                video.increaseAdViewCountBy(count);
                                videoRepository.save(video);
                                System.out.println("광고 조회수 데이터베이스 업데이트 완료: 영상 ID - " + videoId + ", 증가한 광고 조회수 - " + count);
                            });
                            redisTemplate.delete(key);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 키 형식으로 인해 광고 조회수 업데이트를 건너뜁니다: " + key);
                    }
                }
            }
        }
    }


}
