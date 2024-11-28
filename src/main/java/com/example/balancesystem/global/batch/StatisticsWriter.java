package com.example.balancesystem.global.batch;

import com.example.balancesystem.global.videostats.VideoStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class StatisticsWriter implements ItemWriter<VideoStatistics> {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsWriter.class);
    private final JdbcTemplate jdbcTemplate;

    public StatisticsWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends VideoStatistics> items) throws Exception {
        logger.info("Writing statistics for {} items.", items.size());

        String sql = "INSERT INTO video_statistics (video_id, date, view_count, total_play_time, ad_view_count) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "view_count = VALUES(view_count), " +
                "total_play_time = VALUES(total_play_time), " +
                "ad_view_count = VALUES(ad_view_count)";

        // 데이터 검증 및 변환
        List<Object[]> batchArgs = items.getItems().stream()
                .filter(stat -> stat.getVideoId() != null && stat.getDate() != null) // 필수 필드가 null이 아닌지 확인
                .map(stat -> new Object[] {
                        stat.getVideoId(),
                        stat.getDate(),
                        stat.getViewCount() != null ? stat.getViewCount() : 0L, // null일 경우 기본값 설정
                        stat.getTotalPlayTime() != null ? stat.getTotalPlayTime() : 0L,
                        stat.getAdViewCount() != null ? stat.getAdViewCount() : 0L
                })
                .collect(Collectors.toList());

        if (batchArgs.isEmpty()) {
            logger.warn("No valid statistics to write.");
            return;
        }

        // 데이터 삽입
        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);
        logger.info("Successfully processed {} rows.", updateCounts.length);
    }
}
