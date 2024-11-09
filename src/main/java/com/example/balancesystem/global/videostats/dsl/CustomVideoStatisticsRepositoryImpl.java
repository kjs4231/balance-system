package com.example.balancesystem.global.videostats.dsl;

import com.example.balancesystem.global.videostats.QVideoStatistics;
import com.example.balancesystem.global.videostats.VideoStatistics;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class CustomVideoStatisticsRepositoryImpl implements CustomVideoStatisticsRepository {

    private final JPAQueryFactory queryFactory;

    public CustomVideoStatisticsRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<VideoStatistics> findTop5ByDateBetweenOrderByViewCountDesc(LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(QVideoStatistics.videoStatistics)
                .where(QVideoStatistics.videoStatistics.date.between(startDate, endDate))
                .orderBy(QVideoStatistics.videoStatistics.viewCount.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public List<VideoStatistics> findTop5ByDateBetweenOrderByTotalPlayTimeDesc(LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(QVideoStatistics.videoStatistics)
                .where(QVideoStatistics.videoStatistics.date.between(startDate, endDate))
                .orderBy(QVideoStatistics.videoStatistics.totalPlayTime.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public boolean existsByVideoIdAndDate(Long videoId, LocalDate date) {
        Integer count = queryFactory.selectOne()
                .from(QVideoStatistics.videoStatistics)
                .where(QVideoStatistics.videoStatistics.videoId.eq(videoId)
                        .and(QVideoStatistics.videoStatistics.date.eq(date)))
                .fetchFirst();
        return count != null;
    }

    @Override
    public Page<VideoStatistics> findByDate(LocalDate date, Pageable pageable) {
        List<VideoStatistics> content = queryFactory.selectFrom(QVideoStatistics.videoStatistics)
                .where(QVideoStatistics.videoStatistics.date.eq(date))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(QVideoStatistics.videoStatistics)
                .where(QVideoStatistics.videoStatistics.date.eq(date))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<VideoStatistics> findByVideoIdAndDate(Long videoId, LocalDate date) {
        return queryFactory.selectFrom(QVideoStatistics.videoStatistics)
                .where(QVideoStatistics.videoStatistics.videoId.eq(videoId)
                        .and(QVideoStatistics.videoStatistics.date.eq(date)))
                .fetch();
    }

    @Override
    public Long getDailyViewCountByVideoId(Long videoId, LocalDate date) {
        return queryFactory.select(QVideoStatistics.videoStatistics.viewCount.sum())
                .from(QVideoStatistics.videoStatistics)
                .where(QVideoStatistics.videoStatistics.videoId.eq(videoId)
                        .and(QVideoStatistics.videoStatistics.date.eq(date)))
                .fetchOne();
    }

    @Override
    public Long getDailyAdViewCountByVideoId(Long videoId, LocalDate date) {
        return queryFactory.select(QVideoStatistics.videoStatistics.adViewCount.sum())
                .from(QVideoStatistics.videoStatistics)
                .where(QVideoStatistics.videoStatistics.videoId.eq(videoId)
                        .and(QVideoStatistics.videoStatistics.date.eq(date)))
                .fetchOne();
    }
}
