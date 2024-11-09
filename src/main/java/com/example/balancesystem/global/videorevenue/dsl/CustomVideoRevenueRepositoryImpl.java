package com.example.balancesystem.global.videorevenue.dsl;

import com.example.balancesystem.global.videorevenue.QVideoRevenue;
import com.example.balancesystem.global.videorevenue.VideoRevenue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class CustomVideoRevenueRepositoryImpl implements CustomVideoRevenueRepository {

    private final JPAQueryFactory queryFactory;

    public CustomVideoRevenueRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<VideoRevenue> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(QVideoRevenue.videoRevenue)
                .where(QVideoRevenue.videoRevenue.date.between(startDate, endDate))
                .fetch();
    }

    @Override
    public boolean existsByVideoIdAndDate(Long videoId, LocalDate date) {
        Integer count = queryFactory.selectOne()
                .from(QVideoRevenue.videoRevenue)
                .where(QVideoRevenue.videoRevenue.videoId.eq(videoId)
                        .and(QVideoRevenue.videoRevenue.date.eq(date)))
                .fetchFirst();
        return count != null;
    }
}
