package com.example.balancesystem.global.videostats.dsl;

import com.example.balancesystem.global.videostats.VideoStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoStatisticsRepository extends JpaRepository<VideoStatistics, Long>, CustomVideoStatisticsRepository {
}
