package com.example.balancesystem.domain.content.video.dsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomVideoRepository {
    Long getViewCountByVideoId(Long videoId);

    Long getAdViewCountByVideoId(Long videoId);

    List<Long> findAllVideoIds();

    Page<Long> findAllVideoIds(Pageable pageable);

    Long getMinId();
    Long getMaxId();
    List<Long> findVideoIdsByRange(Long minId, Long maxId);
}
