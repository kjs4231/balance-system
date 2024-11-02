package com.example.contentservice.video;

import com.example.contentservice.ad.AdService;
import com.example.contentservice.videohistory.PlayHistory;
import com.example.contentservice.videohistory.PlayHistoryService;
import com.example.global.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final PlayHistoryService playHistoryService;
    private final AdService adService;

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
}
