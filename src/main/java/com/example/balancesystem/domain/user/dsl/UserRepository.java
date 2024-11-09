package com.example.balancesystem.domain.user.dsl;

import com.example.balancesystem.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository, QuerydslPredicateExecutor<User> {
    Boolean existsByUsername(String username);
}
