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
        // 이전 시청 기록 중 가장 최근 기록을 가져옵니다.
        Optional<PlayHistory> optionalPlayHistory = playHistoryRepository.findTopByUserAndVideoAndIsCompletedFalseOrderByViewDateDesc(user, video);

        int startFrom = 0;
        if (optionalPlayHistory.isPresent()) {
            // 이전 시청 기록이 있으면, 마지막 재생 위치를 가져옵니다.
            PlayHistory previousPlayHistory = optionalPlayHistory.get();
            startFrom = previousPlayHistory.getLastPlayedAt();
            // 이전 기록을 완료 상태로 설정
            previousPlayHistory.setCompleted(true);
            playHistoryRepository.save(previousPlayHistory);
        }

        // 새로운 시청 기록 생성
        PlayHistory newPlayHistory = new PlayHistory(user, video, LocalDateTime.now(), startFrom);
        playHistoryRepository.save(newPlayHistory);

        // 비디오 조회수 증가
        video.increaseViewCount();

        return newPlayHistory;
    }

    @Transactional
    public void handlePause(User user, Video video, int currentPlayedAt) {
        // 시청 기록이 없는 경우 새로운 기록 생성
        PlayHistory playHistory = playHistoryRepository.findTopByUserAndVideoAndIsCompletedFalseOrderByViewDateDesc(user, video)
                .orElseGet(() -> {
                    PlayHistory newHistory = new PlayHistory(user, video, LocalDateTime.now(), 0);
                    playHistoryRepository.save(newHistory);
                    return newHistory;
                });

        // 시청 기록의 재생 위치 업데이트
        playHistory.setLastPlayedAt(currentPlayedAt);
        playHistoryRepository.save(playHistory);

        // 재생 위치가 동영상 길이와 같으면 완료로 설정
        if (currentPlayedAt >= video.getDuration()) {
            playHistory.setCompleted(true);
            playHistoryRepository.save(playHistory);
        }
    }

    @Transactional
    public void markCompleted(User user, Video video) {
        // 시청 기록이 없을 경우 새로운 완료된 시청 기록 생성
        PlayHistory playHistory = playHistoryRepository.findTopByUserAndVideoAndIsCompletedFalseOrderByViewDateDesc(user, video)
                .orElseGet(() -> {
                    PlayHistory newHistory = new PlayHistory(user, video, LocalDateTime.now(), 0);
                    newHistory.setCompleted(true);
                    playHistoryRepository.save(newHistory);
                    return newHistory;
                });

        // 시청 기록이 존재할 경우 완료로 설정
        playHistory.setCompleted(true);
        playHistory.setLastPlayedAt(0); // 완료되면 마지막 재생 지점을 0으로 설정
        playHistoryRepository.save(playHistory);
    }


    @Transactional
    public void savePlayHistory(PlayHistory playHistory) {
        playHistoryRepository.save(playHistory);
    }
}
