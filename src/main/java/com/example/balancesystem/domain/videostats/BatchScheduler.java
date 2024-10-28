package com.example.balancesystem.domain.videostats;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job dayStatisticsJob;
    private final Job weekStatisticsJob;
    private final Job monthStatisticsJob;

    // 일간 통계 배치 작업 실행
    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정 실행
    public void runDayStatisticsJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(dayStatisticsJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
