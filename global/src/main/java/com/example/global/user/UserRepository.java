package com.example.global.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);
    User findByUsername(String username);

    User findByUserId(Long userId);
}
