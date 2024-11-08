package com.example.batchservice.videostats;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVideoStatistics is a Querydsl query type for VideoStatistics
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVideoStatistics extends EntityPathBase<VideoStatistics> {

    private static final long serialVersionUID = 1977314198L;

    public static final QVideoStatistics videoStatistics = new QVideoStatistics("videoStatistics");

    public final NumberPath<Long> adViewCount = createNumber("adViewCount", Long.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> statisticId = createNumber("statisticId", Long.class);

    public final NumberPath<Long> totalPlayTime = createNumber("totalPlayTime", Long.class);

    public final NumberPath<Long> videoId = createNumber("videoId", Long.class);

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public QVideoStatistics(String variable) {
        super(VideoStatistics.class, forVariable(variable));
    }

    public QVideoStatistics(Path<? extends VideoStatistics> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVideoStatistics(PathMetadata metadata) {
        super(VideoStatistics.class, metadata);
    }

}

