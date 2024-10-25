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
        // 가장 최근의 미완료된 시청 기록을 가져옵니다.
        Optional<PlayHistory> optionalPlayHistory = playHistoryRepository.findTopByUserAndVideoAndIsCompletedFalseOrderByViewDateDesc(user, video);

        int startFrom = 0;
        if (optionalPlayHistory.isPresent()) {
            // 이전 시청 기록이 있으면, 마지막 재생 위치를 가져옵니다.
            PlayHistory previousPlayHistory = optionalPlayHistory.get();
            startFrom = previousPlayHistory.getLastPlayedAt();

            // 이전 기록을 완료 상태로 설정하고 저장합니다.
            previousPlayHistory.setCompleted(true);
            playHistoryRepository.save(previousPlayHistory);
        }

        // 새로운 시청 기록을 생성하며, playtime을 초기화하고 startFrom을 lastPlayedAt으로 설정
        PlayHistory newPlayHistory = new PlayHistory(user, video, LocalDateTime.now(), startFrom);
        newPlayHistory.setPlayTime(0); 
        newPlayHistory.setLastPlayedAt(startFrom); 
        playHistoryRepository.save(newPlayHistory);

        video.increaseViewCount();

        return newPlayHistory;
    }


    @Transactional
    public void handlePause(User user, Video video, int currentPlayedAt) {
        PlayHistory playHistory = playHistoryRepository.findTopByUserAndVideoAndIsCompletedFalseOrderByViewDateDesc(user, video)
                .orElseThrow(() -> new RuntimeException("시청 기록이 없습니다."));

        // 현재 재생 위치와 저장된 lastPlayedAt의 차이를 순수 시청 시간으로 설정
        int purePlayTime = currentPlayedAt - playHistory.getLastPlayedAt();
        playHistory.setPlayTime(purePlayTime); // 순수 시청 시간 저장
        playHistory.setLastPlayedAt(currentPlayedAt); // 현재 재생 위치를 lastPlayedAt에 업데이트

        // 영상이 끝까지 재생된 경우 완료로 설정
        if (currentPlayedAt >= video.getDuration()) {
            playHistory.setCompleted(true);
        }

        playHistoryRepository.save(playHistory);
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