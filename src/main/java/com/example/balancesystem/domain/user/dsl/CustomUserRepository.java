package com.example.balancesystem.domain.user.dsl;

import com.example.balancesystem.domain.user.User;

public interface CustomUserRepository {
    User findByUsername(String username);
    User findByUserId(Long userId);
}
