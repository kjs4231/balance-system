package com.example.balancesystem.domain.videostats;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VideoStatisticsController {

    private final JobLauncher jobLauncher;
    private final Job dayStatisticsJob;
    private final Job weekStatisticsJob;
    private final Job monthStatisticsJob;

    @GetMapping("/run-day-batch-job")
    public String runDayBatchJob(@RequestParam(value = "time", required = false) Long time) {
        return runBatchJob(dayStatisticsJob, "Day batch job", time);
    }

    @GetMapping("/run-week-batch-job")
    public String runWeekBatchJob(@RequestParam(value = "time", required = false) Long time) {
        return runBatchJob(weekStatisticsJob, "Week batch job", time);
    }

    @GetMapping("/run-month-batch-job")
    public String runMonthBatchJob(@RequestParam(value = "time", required = false) Long time) {
        return runBatchJob(monthStatisticsJob, "Month batch job", time);
    }

    // 공통 배치 잡 실행 로직
    private String runBatchJob(Job job, String jobName, Long time) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", time != null ? time : System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
            return jobName + " has been successfully invoked.";
        } catch (Exception e) {
            e.printStackTrace();
            return jobName + " failed to start due to: " + e.getMessage();
        }
    }
}
