package com.example.balancesystem.domain.video;

import com.example.balancesystem.domain.ad.AdService;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.user.UserRepository;
import com.example.balancesystem.domain.videohistory.PlayHistory;
import com.example.balancesystem.domain.videohistory.PlayHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final PlayHistoryService playHistoryService;
    private final AdService adService;

    @Transactional
    public Video saveVideo(VideoDto videoDto) {
        User owner = userRepository.findById(videoDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Video video = new Video(videoDto.getTitle(), videoDto.getDuration(), owner);
        return videoRepository.save(video);
    }

    @Transactional
    public String playVideo(Long userId, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        PlayHistory playHistory = playHistoryService.handlePlay(user, video);

        // 광고 재생 처리
        handleAdPlayback(video, user, playHistory.getLastPlayedAt());

        int lastPlayedAt = playHistory.getLastPlayedAt();

        return lastPlayedAt == 0 || lastPlayedAt >= video.getDuration()
                ? "동영상을 처음부터 재생합니다."
                : "동영상을 " + lastPlayedAt + "초부터 이어서 재생합니다.";
    }

    @Transactional
    public void pauseVideo(Long userId, Long videoId, int currentPlayedAt) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        playHistoryService.handlePause(user, video, currentPlayedAt);

        // 광고 재생 처리
        handleAdPlayback(video, user, currentPlayedAt);
    }

    // 광고 재생 로직을 AdService로 위임
    private void handleAdPlayback(Video video, User user, int currentPlayedAt) {
        adService.handleAdViews(video, user, currentPlayedAt);
    }
}
