package com.example.contentservice.videohistory.dsl;


import com.example.contentservice.video.Video;
import com.example.contentservice.videohistory.PlayHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

import static com.example.contentservice.videohistory.QPlayHistory.playHistory;
import static com.example.contentservice.adhistory.QAdHistory.adHistory;


@Repository
public class CustomPlayHistoryRepositoryImpl implements CustomPlayHistoryRepository {

    private final JPAQueryFactory queryFactory;

    public CustomPlayHistoryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<PlayHistory> findTopByUserIdAndVideoAndIsCompletedFalseOrderByViewDateDesc(Long userId, Video video) {
        return Optional.ofNullable(
                queryFactory.selectFrom(playHistory)
                        .where(playHistory.userId.eq(userId)
                                .and(playHistory.video.eq(video))
                                .and(playHistory.isCompleted.isFalse()))
                        .orderBy(playHistory.viewDate.desc())
                        .fetchFirst()
        );
    }

    @Override
    public long findPlayTimeByVideoIdAndDate(Long videoId, LocalDate date) {
        return queryFactory.select(playHistory.playTime.sum().coalesce(0))
                .from(playHistory)
                .where(playHistory.video.videoId.eq(videoId)
                        .and(playHistory.viewDate.year().eq(date.getYear()))
                        .and(playHistory.viewDate.month().eq(date.getMonthValue()))
                        .and(playHistory.viewDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();
    }

    @Override
    public long countByVideoIdAndDate(Long videoId, LocalDate date) {
        return queryFactory.select(playHistory.count())
                .from(playHistory)
                .where(playHistory.video.videoId.eq(videoId)
                        .and(playHistory.viewDate.year().eq(date.getYear()))
                        .and(playHistory.viewDate.month().eq(date.getMonthValue()))
                        .and(playHistory.viewDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();
    }

    @Override
    public long countAdViewsByVideoIdAndDate(Long videoId, LocalDate date) {
        return queryFactory.select(adHistory.count())
                .from(adHistory)
                .where(adHistory.video.videoId.eq(videoId)
                        .and(adHistory.viewDate.year().eq(date.getYear()))
                        .and(adHistory.viewDate.month().eq(date.getMonthValue()))
                        .and(adHistory.viewDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();
    }
}
