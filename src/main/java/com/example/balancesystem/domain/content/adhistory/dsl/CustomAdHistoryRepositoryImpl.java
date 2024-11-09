package com.example.balancesystem.domain.content.adhistory.dsl;

import com.example.balancesystem.domain.content.ad.Ad;
import com.example.balancesystem.domain.content.adhistory.QAdHistory;
import com.example.balancesystem.domain.content.video.Video;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class CustomAdHistoryRepositoryImpl implements CustomAdHistoryRepository {

    private final JPAQueryFactory queryFactory;

    public CustomAdHistoryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public boolean existsByUserIdAndAdAndVideoAndViewDate(Long userId, Ad ad, Video video, LocalDate viewDate) {
        Integer fetchOne = queryFactory.selectOne()
                .from(QAdHistory.adHistory)
                .where(QAdHistory.adHistory.userId.eq(userId)
                        .and(QAdHistory.adHistory.ad.eq(ad))
                        .and(QAdHistory.adHistory.video.eq(video))
                        .and(QAdHistory.adHistory.viewDate.eq(viewDate)))
                .fetchFirst();

        return fetchOne != null;
    }
}
