package com.example.contentservice.videohistory;

import com.example.contentservice.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayHistoryService {

    private final PlayHistoryRepository playHistoryRepository;

    @Transactional
    public PlayHistory handlePlay(Long userId, Video video) {
        Optional<PlayHistory> optionalPlayHistory = playHistoryRepository.findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(userId, video);

        int startFrom = 0;
        if (optionalPlayHistory.isPresent()) {
            PlayHistory previousPlayHistory = optionalPlayHistory.get();
            startFrom = previousPlayHistory.getLastPlayedAt();

            previousPlayHistory.setCompleted(true);
            playHistoryRepository.save(previousPlayHistory);
        }

        PlayHistory newPlayHistory = new PlayHistory(userId, video, LocalDateTime.now(), startFrom);
        newPlayHistory.setPlayTime(0);
        newPlayHistory.setLastPlayedAt(startFrom);
        playHistoryRepository.save(newPlayHistory);

        video.increaseViewCount();

        return newPlayHistory;
    }

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

    @Transactional
    public void markCompleted(Long userId, Video video) {
        PlayHistory playHistory = playHistoryRepository.findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(userId, video)
                .orElseGet(() -> {
                    PlayHistory newHistory = new PlayHistory(userId, video, LocalDateTime.now(), 0);
                    newHistory.setCompleted(true);
                    playHistoryRepository.save(newHistory);
                    return newHistory;
                });

        playHistory.setCompleted(true);
        playHistory.setLastPlayedAt(0);
        playHistoryRepository.save(playHistory);
    }

    @Transactional
    public void savePlayHistory(PlayHistory playHistory) {
        playHistoryRepository.save(playHistory);
    }
}
