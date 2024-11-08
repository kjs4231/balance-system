package com.example.contentservice.video;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.contentservice.video.QVideo.video;

@Repository
public class CustomVideoRepositoryImpl implements CustomVideoRepository {

    private final JPAQueryFactory queryFactory;

    public CustomVideoRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Long getViewCountByVideoId(Long videoId) {
        return queryFactory.select(video.viewCount)
                .from(video)
                .where(video.videoId.eq(videoId))
                .fetchOne()
                .longValue();
    }


    @Override
    public Long getAdViewCountByVideoId(Long videoId) {
        return queryFactory.select(video.adViewCount)
                .from(video)
                .where(video.videoId.eq(videoId))
                .fetchOne()
                .longValue();
    }

    @Override
    public List<Long> findAllVideoIds() {
        return queryFactory.select(video.videoId)
                .from(video)
                .fetch();
    }
}
