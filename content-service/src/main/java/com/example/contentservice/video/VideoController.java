package com.example.contentservice.video;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/{videoId}/play")
    public ResponseEntity<String> playVideo(@RequestParam Long userId, @PathVariable Long videoId, HttpServletRequest request) {
        // 요청자 IP를 포함하여 playVideo 호출
        String playMessage = videoService.playVideo(userId, videoId, request);
        return ResponseEntity.ok(playMessage);
    }

    @PostMapping("/{videoId}/pause")
    public ResponseEntity<String> pauseVideo(@RequestParam Long userId, @PathVariable Long videoId, @RequestParam int currentPlayedAt, HttpServletRequest request) {
        // 요청자 IP를 포함하여 pauseVideo 호출
        videoService.pauseVideo(userId, videoId, currentPlayedAt, request);
        return ResponseEntity.ok("동영상 재생을 중단했습니다.");
    }

    // 테스트용 영상 등록.
    @PostMapping("/save")
    public ResponseEntity<Video> saveVideo(@RequestBody VideoDto videoDto) {
        Video savedVideo = videoService.saveVideo(videoDto);
        return ResponseEntity.ok(savedVideo);
    }
}
