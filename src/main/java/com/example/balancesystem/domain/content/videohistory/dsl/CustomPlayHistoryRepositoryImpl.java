package com.example.balancesystem.domain.content.videohistory.dsl;


import com.example.balancesystem.domain.content.adhistory.QAdHistory;
import com.example.balancesystem.domain.content.video.Video;
import com.example.balancesystem.domain.content.videohistory.PlayHistory;
import com.example.balancesystem.domain.content.videohistory.QPlayHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;


@Repository
public class CustomPlayHistoryRepositoryImpl implements CustomPlayHistoryRepository {

    private final JPAQueryFactory queryFactory;

    public CustomPlayHistoryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<PlayHistory> findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(Long userId, Video video) {
        return Optional.ofNullable(
                queryFactory.selectFrom(QPlayHistory.playHistory)
                        .where(QPlayHistory.playHistory.userId.eq(userId)
                                .and(QPlayHistory.playHistory.video.eq(video))
                                .and(QPlayHistory.playHistory.isCompleted.isFalse()))
                        .orderBy(QPlayHistory.playHistory.viewDate.desc())
                        .fetchFirst()
        );
    }

    @Override
    public long findPlayTimeByVideoIdAndDate(Long videoId, LocalDate date) {
        return queryFactory.select(QPlayHistory.playHistory.playTime.sum().coalesce(0))
                .from(QPlayHistory.playHistory)
                .where(QPlayHistory.playHistory.video.videoId.eq(videoId)
                        .and(QPlayHistory.playHistory.viewDate.year().eq(date.getYear()))
                        .and(QPlayHistory.playHistory.viewDate.month().eq(date.getMonthValue()))
                        .and(QPlayHistory.playHistory.viewDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();
    }

    @Override
    public long countByVideoIdAndDate(Long videoId, LocalDate date) {
        return queryFactory.select(QPlayHistory.playHistory.count())
                .from(QPlayHistory.playHistory)
                .where(QPlayHistory.playHistory.video.videoId.eq(videoId)
                        .and(QPlayHistory.playHistory.viewDate.year().eq(date.getYear()))
                        .and(QPlayHistory.playHistory.viewDate.month().eq(date.getMonthValue()))
                        .and(QPlayHistory.playHistory.viewDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();
    }

    @Override
    public long countAdViewsByVideoIdAndDate(Long videoId, LocalDate date) {
        return queryFactory.select(QAdHistory.adHistory.count())
                .from(QAdHistory.adHistory)
                .where(QAdHistory.adHistory.video.videoId.eq(videoId)
                        .and(QAdHistory.adHistory.viewDate.year().eq(date.getYear()))
                        .and(QAdHistory.adHistory.viewDate.month().eq(date.getMonthValue()))
                        .and(QAdHistory.adHistory.viewDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();
    }
}
