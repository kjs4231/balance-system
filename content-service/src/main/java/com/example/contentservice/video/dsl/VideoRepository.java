package com.example.contentservice.video.dsl;

import com.example.contentservice.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long>, CustomVideoRepository {
}
