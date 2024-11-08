package com.example.contentservice.adhistory;

import com.example.contentservice.ad.Ad;
import com.example.contentservice.video.Video;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static com.example.contentservice.adhistory.QAdHistory.adHistory;

@Repository
public class CustomAdHistoryRepositoryImpl implements CustomAdHistoryRepository {

    private final JPAQueryFactory queryFactory;

    public CustomAdHistoryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public boolean existsByUserIdAndAdAndVideoAndViewDate(Long userId, Ad ad, Video video, LocalDate viewDate) {
        Integer fetchOne = queryFactory.selectOne()
                .from(adHistory)
                .where(adHistory.userId.eq(userId)
                        .and(adHistory.ad.eq(ad))
                        .and(adHistory.video.eq(video))
                        .and(adHistory.viewDate.eq(viewDate)))
                .fetchFirst();

        return fetchOne != null;
    }
}
