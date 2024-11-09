package com.example.balancesystem.global.videorevenue.dsl;

import com.example.balancesystem.global.videorevenue.VideoRevenue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRevenueRepository extends JpaRepository<VideoRevenue, Long>, CustomVideoRevenueRepository {
}
