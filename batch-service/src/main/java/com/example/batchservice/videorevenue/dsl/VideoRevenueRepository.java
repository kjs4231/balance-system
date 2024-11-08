package com.example.batchservice.videorevenue.dsl;

import com.example.batchservice.videorevenue.VideoRevenue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRevenueRepository extends JpaRepository<VideoRevenue, Long>, CustomVideoRevenueRepository {
}
