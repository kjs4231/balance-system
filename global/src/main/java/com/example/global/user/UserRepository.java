package com.example.global.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository, QuerydslPredicateExecutor<User> {
    Boolean existsByUsername(String username);
}
