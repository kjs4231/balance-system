package com.example.batchservice.videostats.dsl;

import com.example.batchservice.videostats.VideoStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoStatisticsRepository extends JpaRepository<VideoStatistics, Long>, CustomVideoStatisticsRepository {
}
