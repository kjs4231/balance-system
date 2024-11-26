package com.example.balancesystem.domain.content.video;

import com.example.balancesystem.domain.content.ad.AdService;
import com.example.balancesystem.domain.content.playhistory.PlayHistory;
import com.example.balancesystem.domain.content.playhistory.PlayHistoryService;
import com.example.balancesystem.domain.content.video.Video;
import com.example.balancesystem.domain.content.video.VideoDto;
import com.example.balancesystem.domain.content.video.dsl.VideoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class VideoService {

    private final VideoRepository videoRepository;
    private final PlayHistoryService playHistoryService;
    private final AdService adService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String VIDEO_CACHE_KEY_PREFIX = "video:details:";
    private static final String VIEW_COUNT_KEY_PREFIX = "video:viewCount:";

    private String getVideoCacheKey(Long videoId) {
        return VIDEO_CACHE_KEY_PREFIX + videoId;
    }

    private String getViewCountCacheKey(Long videoId) {
        return VIEW_COUNT_KEY_PREFIX + videoId;
    }

    // Redis multi-get 최적화
    public Map<String, VideoDto> getVideosByIds(List<Long> videoIds) {
        List<String> cacheKeys = videoIds.stream()
                .map(id -> getVideoCacheKey(id))
                .collect(Collectors.toList());

        // Redis에서 여러 키를 동시에 조회
        List<Object> cachedVideos = redisTemplate.opsForValue().multiGet(cacheKeys);

        // 결과 처리
        Map<String, VideoDto> videoMap = new HashMap<>();
        for (int i = 0; i < cachedVideos.size(); i++) {
            if (cachedVideos.get(i) != null) {
                videoMap.put(cacheKeys.get(i), (VideoDto) cachedVideos.get(i));
            }
        }
        return videoMap;
    }

    @Transactional(readOnly = true)
    public VideoDto getVideo(Long videoId) {
        String cacheKey = getVideoCacheKey(videoId);

        // Redis에서 VideoDto 조회
        VideoDto cachedVideo = (VideoDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedVideo != null) {
            long cacheAge = System.currentTimeMillis() - redisTemplate.getExpire(cacheKey, TimeUnit.MILLISECONDS);
            // 5분 이내라면 캐시 사용
            if (cacheAge < 300000) {
                return cachedVideo;
            }
        }

        // Redis에 데이터 없으면 DB에서 조회
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));

        // 비디오 정보를 Redis에 캐시 (캐시 갱신)
        VideoDto videoDto = new VideoDto(video.getTitle(), video.getDuration(), video.getOwnerId());
        redisTemplate.opsForValue().set(cacheKey, videoDto, 10, TimeUnit.MINUTES);

        return videoDto;
    }

    @Transactional
    public Video saveVideo(VideoDto videoDto) {
        Long ownerId = videoDto.getOwnerId();
        Video video = new Video(videoDto.getTitle(), videoDto.getDuration(), ownerId);
        return videoRepository.save(video);
    }

    @Transactional
    public String playVideo(Long userId, Long videoId, HttpServletRequest request) {
        // 여기서 여러 비디오를 조회할 때 getVideosByIds를 사용
        Map<String, VideoDto> videoDtos = getVideosByIds(List.of(videoId));

        // 비디오가 캐시되지 않으면 DB에서 조회
        VideoDto videoDto = videoDtos.get(getVideoCacheKey(videoId));
        if (videoDto == null) {
            videoDto = getVideo(videoId);
        }

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));

        PlayHistory playHistory = playHistoryService.handlePlay(userId, video, request);
        adService.handleAdViews(video, userId, playHistory.getLastPlayedAt(), request);

        int lastPlayedAt = playHistory.getLastPlayedAt();
        return lastPlayedAt == 0 || lastPlayedAt >= videoDto.getDuration()
                ? "동영상을 처음부터 재생합니다."
                : "동영상을 " + lastPlayedAt + "초부터 이어서 재생합니다.";
    }

    @Transactional
    public void pauseVideo(Long userId, Long videoId, int currentPlayedAt, HttpServletRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));

        playHistoryService.handlePause(userId, video, currentPlayedAt);
        adService.handleAdViews(video, userId, currentPlayedAt, request);
    }

    // 조회수 동기화 작업을 비동기적으로 처리
    @Scheduled(fixedRate = 60000)
    public void syncViewCountsToDatabase() {
        Set<String> viewKeys = redisTemplate.keys(VIEW_COUNT_KEY_PREFIX + "*");
        if (viewKeys != null) {
            for (String key : viewKeys) {
                try {
                    String videoIdStr = key.replace(VIEW_COUNT_KEY_PREFIX, "");
                    Long videoId = Long.parseLong(videoIdStr);

                    Object countObj = redisTemplate.opsForValue().get(key);
                    if (countObj != null) {
                        int count;
                        if (countObj instanceof Integer) {
                            count = (Integer) countObj;
                        } else if (countObj instanceof String) {
                            count = Integer.parseInt((String) countObj);
                        } else {
                            throw new IllegalStateException("Unexpected data type for view count: " + countObj.getClass());
                        }

                        // 비동기적으로 조회수 동기화
                        syncViewCountAsync(videoId, count);
                    }
                } catch (Exception e) {
                    System.out.println("조회수 동기화 오류: " + e.getMessage());
                }
            }
        }
    }

    // 비동기적으로 조회수 업데이트
    @Async
    public void syncViewCountAsync(Long videoId, int count) {
        videoRepository.findById(videoId).ifPresent(video -> {
            video.increaseViewCountBy(count);
            videoRepository.save(video);
        });
        redisTemplate.delete(getViewCountCacheKey(videoId));
    }
}
