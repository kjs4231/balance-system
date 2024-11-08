package com.example.batchservice.revenuerate.dsl;

import com.example.batchservice.revenuerate.RevenueRate;
import com.example.batchservice.revenuerate.RevenueType;

import java.util.List;

public interface CustomRevenueRateRepository {
    List<RevenueRate> findAllByTypeOrderByMinViewsAsc(RevenueType type);
}
