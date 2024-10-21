package com.example.balancesystem.domain.video;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.ad.AdRepository;
import com.example.balancesystem.domain.ad.AdService;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.user.UserRepository;
import com.example.balancesystem.domain.videoad.VideoAd;
import com.example.balancesystem.domain.videoad.VideoAdRepository;
import com.example.balancesystem.domain.videohistory.PlayHistory;
import com.example.balancesystem.domain.videohistory.PlayHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;



@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final PlayHistoryService playHistoryService;
    private final VideoAdRepository videoAdRepository;
    private final AdRepository adRepository;
    private final AdService adService;

    @Transactional
    public String playVideo(Long userId, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 항상 새로운 시청 기록 생성
        PlayHistory playHistory = playHistoryService.handlePlay(user, video);

        // 광고 처리 로직 추가
        adService.handleAdViews(video, user, playHistory.getLastPlayedAt());

        int lastPlayedAt;

        // 기존 시청 기록이 있다면 마지막 재생 시점(lastPlayedAt)을 가져와서 재생 시작 위치로 사용
        if (playHistory.getPlayTime() != 0) {
            lastPlayedAt = playHistory.getPlayTime(); // 이전 재생 기록의 시간을 사용
        } else {
            lastPlayedAt = playHistory.getLastPlayedAt(); // 새로운 row일 경우
        }

        // 시청 기록에 따른 메시지 처리
        if (lastPlayedAt == 0) {
            return "동영상을 처음부터 재생합니다.";
        } else {
            return "동영상을 " + lastPlayedAt + "초부터 이어서 재생합니다.";
        }
    }

    @Transactional
    public void pauseVideo(Long userId, Long videoId, int currentPlayedAt) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 영상이 끝까지 재생되었는지 확인
        if (currentPlayedAt >= video.getDuration()) {
            playHistoryService.markCompleted(user, video);  // 완료 상태로 마킹

            // 영상 끝까지 본 경우에도 광고 시청 처리 한 번 더 수행
            adService.handleAdViews(video, user, currentPlayedAt);

            // 새로운 기록을 추가하지 않고 메서드 종료
            return;
        }

        // 영상이 끝까지 재생되지 않은 경우, 중지된 시점에서 새로운 기록을 남김
        playHistoryService.handlePause(user, video, currentPlayedAt);

        // 광고 시청 상태 처리 추가
        adService.handleAdViews(video, user, currentPlayedAt);
    }



    // 동영상과 광고 저장 메서드
    @Transactional
    public Video saveVideoWithAds(VideoDto videoDto) {
        User owner = userRepository.findById(videoDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Video video = new Video(videoDto.getTitle(), videoDto.getDuration(), owner);
        videoRepository.save(video);

        // 광고 추가
        addAdsToVideo(video);

        return video;
    }

    // 동영상 길이에 따른 광고 추가 로직
    private void addAdsToVideo(Video video) {
        int duration = video.getDuration();

        // 첫 번째 광고는 5분이 넘으면 추가
        if (duration >= 300) {
            Ad ad1 = new Ad();
            adRepository.save(ad1);

            VideoAd videoAd1 = new VideoAd(video, ad1);
            videoAdRepository.save(videoAd1);
            video.getVideoAds().add(videoAd1);
        }

        // 두 번째 광고는 10분이 넘으면 추가
        if (duration >= 600) {
            Ad ad2 = new Ad();
            adRepository.save(ad2);

            VideoAd videoAd2 = new VideoAd(video, ad2);
            videoAdRepository.save(videoAd2);
            video.getVideoAds().add(videoAd2);
        }

        // 동영상 저장 (추가된 광고 정보 포함)
        videoRepository.save(video);
    }
}
