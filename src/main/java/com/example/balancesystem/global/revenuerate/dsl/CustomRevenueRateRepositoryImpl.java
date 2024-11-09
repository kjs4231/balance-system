package com.example.balancesystem.global.revenuerate.dsl;

import com.example.balancesystem.global.revenuerate.QRevenueRate;
import com.example.balancesystem.global.revenuerate.RevenueRate;
import com.example.balancesystem.global.revenuerate.RevenueType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomRevenueRateRepositoryImpl implements CustomRevenueRateRepository {

    private final JPAQueryFactory queryFactory;

    public CustomRevenueRateRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<RevenueRate> findAllByTypeOrderByMinViewsAsc(RevenueType type) {
        return queryFactory.selectFrom(QRevenueRate.revenueRate)
                .where(QRevenueRate.revenueRate.type.eq(type))
                .orderBy(QRevenueRate.revenueRate.minViews.asc())
                .fetch();
    }
}
