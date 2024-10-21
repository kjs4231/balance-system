package com.example.balancesystem.domain.videohistory;

import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
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
    public PlayHistory handlePlay(User user, Video video) {
        // 이전 시청 기록이 완료되지 않았더라도 항상 새로운 시청 기록을 생성합니다.
        // 먼저 마지막 시청 기록을 가져옵니다.
        PlayHistory previousPlayHistory = playHistoryRepository.findByUserAndVideoAndIsCompletedFalse(user, video)
                .orElse(null);

        int startFrom = 0;
        if (previousPlayHistory != null) {
            // 이전 시청 기록이 있으면, 마지막 재생 위치를 가져옵니다.
            startFrom = previousPlayHistory.getLastPlayedAt();
        }

        // 새로운 시청 기록 생성
        PlayHistory newPlayHistory = new PlayHistory(user, video, LocalDateTime.now(), startFrom);
        playHistoryRepository.save(newPlayHistory);

        // 비디오 조회수 증가
        video.increaseViewCount();

        return newPlayHistory;
    }


    private PlayHistory createNewPlayHistory(User user, Video video, int startFrom) {
        PlayHistory newHistory = new PlayHistory(user, video, LocalDateTime.now(), startFrom);
        playHistoryRepository.save(newHistory);
        return newHistory;
    }



    @Transactional
    public void handlePause(User user, Video video, int currentPlayedAt) {
        PlayHistory playHistory = playHistoryRepository.findByUserAndVideoAndIsCompletedFalse(user, video)
                .orElseThrow(() -> new RuntimeException("시청 기록을 찾을 수 없습니다"));
        playHistory.setLastPlayedAt(currentPlayedAt);
        playHistoryRepository.save(playHistory);

        // 재생 위치가 동영상 길이와 같으면 완료로 설정
        if (currentPlayedAt >= video.getDuration()) {
            playHistory.setCompleted(true);
        }
    }

    @Transactional
    public void markCompleted(User user, Video video) {
        PlayHistory playHistory = playHistoryRepository.findByUserAndVideoAndIsCompletedFalse(user, video)
                .orElseThrow(() -> new RuntimeException("시청 기록을 찾을 수 없습니다"));
        playHistory.setCompleted(true);
        playHistory.setLastPlayedAt(0); // 완료되면 마지막 재생 지점을 0으로 설정
        playHistoryRepository.save(playHistory);
    }

    @Transactional
    public void savePlayHistory(PlayHistory playHistory) {
        playHistoryRepository.save(playHistory);
    }
}