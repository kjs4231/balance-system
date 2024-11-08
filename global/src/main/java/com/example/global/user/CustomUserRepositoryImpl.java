package com.example.global.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import static com.example.global.user.QUser.user;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    public CustomUserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public User findByUsername(String username) {
        return queryFactory.selectFrom(user)
                .where(user.username.eq(username))
                .fetchOne();
    }

    @Override
    public User findByUserId(Long userId) {
        return queryFactory.selectFrom(user)
                .where(user.userId.eq(userId))
                .fetchOne();
    }
}
