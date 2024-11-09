package com.example.balancesystem.domain.content.video.dsl;

import com.example.balancesystem.domain.content.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long>, CustomVideoRepository {
}
