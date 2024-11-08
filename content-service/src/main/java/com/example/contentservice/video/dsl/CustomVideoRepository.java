package com.example.contentservice.video.dsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomVideoRepository {

    Long getViewCountByVideoId(Long videoId);

    Long getAdViewCountByVideoId(Long videoId);

    List<Long> findAllVideoIds();

    Page<Long> findAllVideoIds(Pageable pageable);
}
