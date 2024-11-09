package com.example.batchservice.batch;

import com.example.batchservice.videostats.VideoStatistics;
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
        logger.info("통계 size: {}", items.size());

        String sql = "INSERT INTO video_statistics (video_id, date, view_count, total_play_time, ad_view_count) " +
                "VALUES (?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = items.getItems().stream()
                .map(stat -> new Object[] {
                        stat.getVideoId(),
                        stat.getDate(),
                        stat.getViewCount(),
                        stat.getTotalPlayTime(),
                        stat.getAdViewCount()
                })
                .collect(Collectors.toList());

        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);
        logger.info("Inserted rows: {}", updateCounts.length);
    }
}
