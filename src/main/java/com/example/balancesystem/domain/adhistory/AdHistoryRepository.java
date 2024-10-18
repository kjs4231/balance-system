package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdHistoryRepository extends JpaRepository<AdHistory, Long> {
    boolean existsByUserAndAd(User user, Ad ad);
}
