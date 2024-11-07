package com.example.contentservice.video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v.viewCount FROM Video v WHERE v.videoId = :videoId")
    Long getViewCountByVideoId(@Param("videoId") Long videoId);

    @Query("SELECT v.adViewCount FROM Video v WHERE v.videoId = :videoId")
    Long getAdViewCountByVideoId(@Param("videoId") Long videoId);

    @Query("SELECT v.videoId FROM Video v")
    List<Long> findAllVideoIds();

}
