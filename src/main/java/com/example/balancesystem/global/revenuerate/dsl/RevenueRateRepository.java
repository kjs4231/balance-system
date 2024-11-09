package com.example.balancesystem.global.revenuerate.dsl;

import com.example.balancesystem.global.revenuerate.RevenueRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueRateRepository extends JpaRepository<RevenueRate, Long>, CustomRevenueRateRepository {
}
