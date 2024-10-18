package com.example.balancesystem.domain.videohistory;


import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayHistoryService {

    private final PlayHistoryRepository playHistoryRepository;

    @Transactional
    public PlayHistory handlePlay(User user, Video video) {
        PlayHistory playHistory = playHistoryRepository.findByUserAndVideo(user, video)
                .orElseGet(() -> playHistoryRepository.save(new PlayHistory(user, video)));

        if (playHistory.getLastPlayedAt() == 0) {
            video.increaseViewCount();
        }

        if (playHistory.isCompleted()) {
            playHistory.setCompleted(false);
            playHistoryRepository.save(playHistory);
        }

        return playHistory;
    }


    @Transactional
    public void handlePause(User user, Video video, int currentPlayedAt) {
        PlayHistory playHistory = playHistoryRepository.findByUserAndVideo(user, video)
                .orElseThrow(() -> new RuntimeException("재생 기록을 찾을 수 없습니다"));

        playHistory.setLastPlayedAt(currentPlayedAt);
        playHistory.setCompleted(false);
        playHistoryRepository.save(playHistory);
    }

    @Transactional
    public void markCompleted(User user, Video video) {
        PlayHistory playHistory = playHistoryRepository.findByUserAndVideo(user, video)
                .orElseThrow(() -> new RuntimeException("재생 기록을 찾을 수 없습니다"));

        playHistory.setCompleted(true);
        playHistoryRepository.save(playHistory);
    }

}
