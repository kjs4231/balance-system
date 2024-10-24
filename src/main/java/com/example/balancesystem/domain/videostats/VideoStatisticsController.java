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
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", time != null ? time : System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(dayStatisticsJob, jobParameters);
            return "Day batch job has been invoked.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Day batch job failed to start.";
        }
    }

    @GetMapping("/run-week-batch-job")
    public String runWeekBatchJob(@RequestParam(value = "time", required = false) Long time) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", time != null ? time : System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(weekStatisticsJob, jobParameters);
            return "Week batch job has been invoked.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Week batch job failed to start.";
        }
    }

    @GetMapping("/run-month-batch-job")
    public String runMonthBatchJob(@RequestParam(value = "time", required = false) Long time) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", time != null ? time : System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(monthStatisticsJob, jobParameters);
            return "Month batch job has been invoked.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Month batch job failed to start.";
        }
    }
}
