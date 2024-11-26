package com.example.balancesystem.domain.content.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-data")
@RequiredArgsConstructor
public class TestDataController {

    private final TestDataService testDataService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateTestData() {

        // 비디오 데이터 생성
        testDataService.generateTestVideos();

        // 광고 데이터 생성
        testDataService.generateTestAds();

        // 영상 시청 기록 및 광고 시청 기록 생성
        testDataService.generatePlayAndAdHistories();

        return ResponseEntity.ok("고정된 테스트 데이터가 성공적으로 생성되었습니다.");
    }
}
