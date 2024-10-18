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

//    @Transactional
//    public String playVideo(Long userId, Long videoId) {
//        Video video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
//
//        // 영상 재생 시작 (광고 시청 처리 없음)
//        return playHistoryService.handlePlay(user, video).getLastPlayedAt() == 0
//                ? "동영상을 처음부터 재생합니다."
//                : "동영상을 이어서 재생합니다.";
//    }
    @Transactional
    public String playVideo(Long userId, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        PlayHistory playHistory = playHistoryService.handlePlay(user, video);

        if (playHistory.getLastPlayedAt() == 0) {
            video.increaseViewCount();
            return "동영상을 처음부터 재생합니다.";
        } else {
            return "동영상을 " + playHistory.getLastPlayedAt() + "초부터 이어서 재생합니다.";
        }
    }

    @Transactional
    public void pauseVideo(Long userId, Long videoId, int currentPlayedAt) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 영상 정지 시 광고 시청 처리
        adService.handleAdViews(video, user, currentPlayedAt);

        // 영상 정지 이력 저장
        playHistoryService.handlePause(user, video, currentPlayedAt);

        // 영상이 끝까지 재생된 경우 isCompleted를 true로 설정
        if (currentPlayedAt >= video.getDuration()) {
            playHistoryService.markCompleted(user, video);
        }
    }



}
