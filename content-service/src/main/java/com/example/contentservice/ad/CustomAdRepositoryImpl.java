package com.example.contentservice.ad;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.contentservice.ad.QAd.ad;

@Repository
public class CustomAdRepositoryImpl implements CustomAdRepository {

    private final JPAQueryFactory queryFactory;

    public CustomAdRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Ad> findAllAds() {
        return queryFactory.selectFrom(ad)
                .fetch();
    }
}
