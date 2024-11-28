package com.example.balancesystem.domain.content.video.dsl;

import com.example.balancesystem.domain.content.video.QVideo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomVideoRepositoryImpl implements CustomVideoRepository {

    private final JPAQueryFactory queryFactory;

    public CustomVideoRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Long getViewCountByVideoId(Long videoId) {
        return queryFactory.select(QVideo.video.viewCount)
                .from(QVideo.video)
                .where(QVideo.video.videoId.eq(videoId))
                .fetchOne()
                .longValue();
    }

    @Override
    public Long getAdViewCountByVideoId(Long videoId) {
        return queryFactory.select(QVideo.video.adViewCount)
                .from(QVideo.video)
                .where(QVideo.video.videoId.eq(videoId))
                .fetchOne()
                .longValue();
    }

    @Override
    public List<Long> findAllVideoIds() {
        return queryFactory.select(QVideo.video.videoId)
                .from(QVideo.video)
                .fetch();
    }

    @Override
    public Page<Long> findAllVideoIds(Pageable pageable) {
        List<Long> content = queryFactory.select(QVideo.video.videoId)
                .from(QVideo.video)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(QVideo.video.videoId.count())
                .from(QVideo.video)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Long getMinId() {
        return queryFactory.select(QVideo.video.videoId.min())
                .from(QVideo.video)
                .fetchOne();
    }

    @Override
    public Long getMaxId() {
        return queryFactory.select(QVideo.video.videoId.max())
                .from(QVideo.video)
                .fetchOne();
    }

    @Override
    public List<Long> findVideoIdsByRange(Long minId, Long maxId) {
        return queryFactory.select(QVideo.video.videoId)
                .from(QVideo.video)
                .where(QVideo.video.videoId.between(minId, maxId))
                .fetch();
    }
}
