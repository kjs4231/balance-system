package com.example.balancesystem.domain.content.test;

import com.example.balancesystem.domain.content.ad.Ad;
import com.example.balancesystem.domain.content.ad.dsl.AdRepository;
import com.example.balancesystem.domain.content.adhistory.AdHistory;
import com.example.balancesystem.domain.content.adhistory.dsl.AdHistoryRepository;
import com.example.balancesystem.domain.content.playhistory.PlayHistory;
import com.example.balancesystem.domain.content.playhistory.dsl.PlayHistoryRepository;
import com.example.balancesystem.domain.content.video.Video;
import com.example.balancesystem.domain.content.video.dsl.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TestDataService {

    private final VideoRepository videoRepository;
    private final AdRepository adRepository;
    private final PlayHistoryRepository playHistoryRepository;
    private final AdHistoryRepository adHistoryRepository;

    private static final int VIDEO_COUNT = 6400; // 비디오 개수 고정
    private static final int PLAY_HISTORY_COUNT = 5000000; // 시청 기록 개수 고정
    private static final LocalDate FIXED_DATE = LocalDate.of(2024, 11, 27); // 고정된 날짜

    // 1. 비디오 데이터 생성
    public void generateTestVideos() {
        List<Video> videos = new ArrayList<>();
        for (int i = 1; i <= VIDEO_COUNT; i++) {
            videos.add(new Video("Test Video " + i, 600, (long) (i % 8 + 1))); // Owner ID는 1~8 순환
        }
        videoRepository.saveAll(videos);
        System.out.println(VIDEO_COUNT + "개의 비디오 데이터가 생성되었습니다.");
    }

    // 2. 광고 데이터 생성 (500개 고정)
    public void generateTestAds() {
        List<Ad> ads = new ArrayList<>();
        for (int i = 1; i <= 500; i++) {
            ads.add(new Ad());
        }
        adRepository.saveAll(ads);
        System.out.println("500개의 광고 데이터가 생성되었습니다.");
    }

    // 3. 영상 시청 기록 및 광고 시청 기록 생성
    public void generatePlayAndAdHistories() {
        List<PlayHistory> playHistories = new ArrayList<>();
        List<AdHistory> adHistories = new ArrayList<>();
        List<Video> videos = videoRepository.findAll();
        List<Ad> ads = adRepository.findAll();

        // 랜덤 광고 매칭
        Map<Video, List<Ad>> videoAdMap = new HashMap<>();
        Random random = new Random();

        for (Video video : videos) {
            List<Ad> matchedAds = new ArrayList<>();
            int firstAdIndex = random.nextInt(ads.size()); // 첫 번째 광고 랜덤 선택
            int secondAdIndex;

            // 두 번째 광고는 첫 번째 광고와 다른 광고 선택
            do {
                secondAdIndex = random.nextInt(ads.size());
            } while (secondAdIndex == firstAdIndex);

            matchedAds.add(ads.get(firstAdIndex));
            matchedAds.add(ads.get(secondAdIndex));
            videoAdMap.put(video, matchedAds);
        }

        int[] adPlayTimes = {300, 600}; // 광고 재생 지점 (초 단위)

        for (int i = 1; i <= PLAY_HISTORY_COUNT; i++) {
            Long userId = (long) (i % 8 + 1); // User ID는 1~8 순환
            Video video = videos.get(i % videos.size()); // Video는 순환 참조
            LocalDateTime viewDateTime = FIXED_DATE.atStartOfDay().plusMinutes(i % 1440); // 고정된 날짜 안에서 시청 시간 분산

            // 영상 시청 기록 생성
            int lastPlayedAt = i % 600; // 시청 위치 (0~600 초)
            PlayHistory playHistory = new PlayHistory(userId, video, viewDateTime, lastPlayedAt);
            playHistories.add(playHistory);

            // 영상 조회수 증가
            video.increaseViewCount();

            // 광고 시청 기록 생성
            List<Ad> matchedAds = videoAdMap.get(video);
            for (int adIndex = 0; adIndex < matchedAds.size(); adIndex++) {
                if (lastPlayedAt >= adPlayTimes[adIndex]) {
                    Ad ad = matchedAds.get(adIndex); // 매칭된 광고 가져오기
                    AdHistory adHistory = new AdHistory(userId, ad, video, FIXED_DATE);
                    adHistories.add(adHistory);

                    // 광고 조회수 증가
                    video.increaseAdViewCount();
                }
            }

            // 주기적으로 저장하여 메모리 부담 최소화
            if (i % 10000 == 0) {
                playHistoryRepository.saveAll(playHistories);
                adHistoryRepository.saveAll(adHistories);
                playHistories.clear();
                adHistories.clear();
                System.out.println(i + "개의 기록이 저장되었습니다.");
            }
        }

        // 남은 기록 저장
        playHistoryRepository.saveAll(playHistories);
        adHistoryRepository.saveAll(adHistories);
        videoRepository.saveAll(videos); // 조회수 업데이트

        System.out.println(PLAY_HISTORY_COUNT + "개의 영상 시청 기록과 그에 따른 광고 시청 기록이 생성되었습니다.");
    }
}
