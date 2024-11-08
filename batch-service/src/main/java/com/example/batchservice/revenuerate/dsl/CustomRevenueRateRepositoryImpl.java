package com.example.batchservice.revenuerate.dsl;

import com.example.batchservice.revenuerate.RevenueRate;
import com.example.batchservice.revenuerate.RevenueType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.batchservice.revenuerate.QRevenueRate.revenueRate;

@Repository
public class CustomRevenueRateRepositoryImpl implements CustomRevenueRateRepository {

    private final JPAQueryFactory queryFactory;

    public CustomRevenueRateRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<RevenueRate> findAllByTypeOrderByMinViewsAsc(RevenueType type) {
        return queryFactory.selectFrom(revenueRate)
                .where(revenueRate.type.eq(type))
                .orderBy(revenueRate.minViews.asc())
                .fetch();
    }
}
