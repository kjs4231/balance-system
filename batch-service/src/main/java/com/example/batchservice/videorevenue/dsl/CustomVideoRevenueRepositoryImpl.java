package com.example.batchservice.videorevenue.dsl;

import com.example.batchservice.videorevenue.VideoRevenue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.example.batchservice.videorevenue.QVideoRevenue.videoRevenue;

@Repository
public class CustomVideoRevenueRepositoryImpl implements CustomVideoRevenueRepository {

    private final JPAQueryFactory queryFactory;

    public CustomVideoRevenueRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<VideoRevenue> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(videoRevenue)
                .where(videoRevenue.date.between(startDate, endDate))
                .fetch();
    }

    @Override
    public boolean existsByVideoIdAndDate(Long videoId, LocalDate date) {
        Integer count = queryFactory.selectOne()
                .from(videoRevenue)
                .where(videoRevenue.videoId.eq(videoId)
                        .and(videoRevenue.date.eq(date)))
                .fetchFirst();
        return count != null;
    }
}
