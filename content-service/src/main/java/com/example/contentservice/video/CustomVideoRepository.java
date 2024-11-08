package com.example.contentservice.video;

import java.util.List;

public interface CustomVideoRepository {
    Long getViewCountByVideoId(Long videoId);
    Long getAdViewCountByVideoId(Long videoId);
    List<Long> findAllVideoIds();
}
