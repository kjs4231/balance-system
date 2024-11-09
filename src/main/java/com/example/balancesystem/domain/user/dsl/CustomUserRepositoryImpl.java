package com.example.balancesystem.domain.user.dsl;

import com.example.balancesystem.domain.user.QUser;
import com.example.balancesystem.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    public CustomUserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public User findByUsername(String username) {
        return queryFactory.selectFrom(QUser.user)
                .where(QUser.user.username.eq(username))
                .fetchOne();
    }

    @Override
    public User findByUserId(Long userId) {
        return queryFactory.selectFrom(QUser.user)
                .where(QUser.user.userId.eq(userId))
                .fetchOne();
    }
}
