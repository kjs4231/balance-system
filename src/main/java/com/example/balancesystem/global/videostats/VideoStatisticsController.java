package com.example.balancesystem.global.videostats;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VideoStatisticsController {

    private final JobLauncher jobLauncher;
    private final Job dayStatisticsJob;
    private final VideoStatisticsService videoStatisticsService;

    @GetMapping("/top5/view-count")
    public Map<String, List<String>> getTop5ByViewCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return videoStatisticsService.getTop5ByViewCount(date);
    }

    @GetMapping("/top5/play-time")
    public Map<String, List<String>> getTop5ByPlayTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return videoStatisticsService.getTop5ByPlayTime(date);
    }

    @GetMapping("/run-day-batch-job")
    public String runDayBatchJob(@RequestParam(value = "date", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return runBatchJob(dayStatisticsJob, "Day batch job", date);
    }

    // 배치 잡 실행 로직
    private String runBatchJob(Job job, String jobName, LocalDate date) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", date != null ? date.toString() : LocalDate.now().toString())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
            return jobName + " has been successfully invoked.";
        } catch (Exception e) {
            e.printStackTrace();
            return jobName + " failed to start due to: " + e.getMessage();
        }
    }
}
