package com.example.balancesystem.domain.revenuerate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RevenueRateRepository extends JpaRepository<RevenueRate, Long> {

    List<RevenueRate> findAllByTypeOrderByMinViewsAsc(@Param("type") RevenueType type);
}
