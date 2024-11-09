package com.example.balancesystem.domain.content.ad.dsl;

import com.example.balancesystem.domain.content.ad.Ad;
import com.example.balancesystem.domain.content.ad.QAd;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomAdRepositoryImpl implements CustomAdRepository {

    private final JPAQueryFactory queryFactory;

    public CustomAdRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Ad> findAll() {
        return queryFactory.selectFrom(QAd.ad)
                .fetch();
    }
}
