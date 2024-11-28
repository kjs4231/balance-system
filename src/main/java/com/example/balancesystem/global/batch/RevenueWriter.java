package com.example.balancesystem.global.batch;

import com.example.balancesystem.global.videorevenue.VideoRevenue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RevenueWriter implements ItemWriter<VideoRevenue> {

    private static final Logger logger = LoggerFactory.getLogger(RevenueWriter.class);
    private final JdbcTemplate jdbcTemplate;

    public RevenueWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends VideoRevenue> items) throws Exception {
        logger.info("Writing {} revenue records", items.size());

        String sql = "INSERT INTO video_revenue (video_id, date, view_revenue, ad_revenue, total_revenue) " +
                "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "view_revenue = VALUES(view_revenue), ad_revenue = VALUES(ad_revenue), total_revenue = VALUES(total_revenue)";

        List<Object[]> batchArgs = items.getItems().stream()
                .map(revenue -> new Object[] {
                        revenue.getVideoId(),
                        revenue.getDate(),
                        revenue.getViewRevenue(),
                        revenue.getAdRevenue(),
                        revenue.getTotalRevenue()
                })
                .collect(Collectors.toList());

        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);
        logger.info("Rows affected: {}", updateCounts.length);
    }
}
