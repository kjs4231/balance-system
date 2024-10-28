package com.example.balancesystem.domain.revenuerate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RevenueRateRepository extends JpaRepository<RevenueRate, Long> {

    @Query("SELECT r FROM RevenueRate r WHERE r.type = :type AND r.minViews <= :views " +
            "AND (r.maxViews IS NULL OR r.maxViews > :views)")
    RevenueRate findRateByViewsAndType(@Param("views") Long views, @Param("type") RevenueType type);
}
