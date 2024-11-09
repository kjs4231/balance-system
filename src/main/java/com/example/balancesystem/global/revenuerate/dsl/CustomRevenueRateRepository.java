package com.example.balancesystem.global.revenuerate.dsl;

import com.example.balancesystem.global.revenuerate.RevenueRate;
import com.example.balancesystem.global.revenuerate.RevenueType;

import java.util.List;

public interface CustomRevenueRateRepository {
    List<RevenueRate> findAllByTypeOrderByMinViewsAsc(RevenueType type);
}
