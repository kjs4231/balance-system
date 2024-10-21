package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdHistoryRepository extends JpaRepository<AdHistory, Long> {
    boolean existsByUserAndAdAndViewedTrue(User user, Ad ad);
    Optional<AdHistory> findByUserAndAd(User user, Ad ad);

    boolean existsByUserAndAd(User user, Ad ad);
}
