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

import java.util.List;

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
    public Video saveVideoWithAds(VideoDto videoDto) {
        User owner = userRepository.findById(videoDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Video video = new Video(videoDto.getTitle(), videoDto.getDuration(), owner);
        videoRepository.save(video);

        // 광고 추가 - 랜덤 광고 매칭
        addRandomAdsToVideo(video);

        return video;
    }

    private void addRandomAdsToVideo(Video video) {
        int duration = video.getDuration();

        // 첫 번째 광고는 5분이 넘으면 추가
        if (duration >= 300) {
            addAdToVideo(video);
        }

        // 두 번째 광고는 10분이 넘으면 추가
        if (duration >= 600) {
            addAdToVideo(video);
        }

        videoRepository.save(video);
    }

    private void addAdToVideo(Video video) {
        Ad randomAd = getRandomAd();
        VideoAd videoAd = new VideoAd(video, randomAd);
        videoAdRepository.save(videoAd);
        video.getVideoAds().add(videoAd);
    }

    private Ad getRandomAd() {
        List<Ad> ads = adRepository.findAll();

        if (ads.isEmpty()) {
            throw new RuntimeException("등록된 광고가 없습니다");
        }

        int randomIndex = (int) (Math.random() * ads.size());
        return ads.get(randomIndex);
    }

    @Transactional
    public String playVideo(Long userId, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        PlayHistory playHistory = playHistoryService.handlePlay(user, video);

        handleAdPlayback(video, user, playHistory.getLastPlayedAt());

        int lastPlayedAt = playHistory.getLastPlayedAt();

        // 마지막 시청 위치가 0이거나 영상 길이를 초과하면 처음부터 재생
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
        handleAdPlayback(video, user, currentPlayedAt);
    }


    private void handleAdPlayback(Video video, User user, int currentPlayedAt) {
        adService.handleAdViews(video, user, currentPlayedAt);
    }
}
