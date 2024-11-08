package com.example.batchservice.revenuerate.dsl;

import com.example.batchservice.revenuerate.RevenueRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueRateRepository extends JpaRepository<RevenueRate, Long>, CustomRevenueRateRepository {
}
