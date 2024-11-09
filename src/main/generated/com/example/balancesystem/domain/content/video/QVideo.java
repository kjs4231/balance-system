package com.example.balancesystem.domain.content.video;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVideo is a Querydsl query type for Video
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVideo extends EntityPathBase<Video> {

    private static final long serialVersionUID = -741000291L;

    public static final QVideo video = new QVideo("video");

    public final ListPath<com.example.balancesystem.domain.content.adhistory.AdHistory, com.example.balancesystem.domain.content.adhistory.QAdHistory> adHistories = this.<com.example.balancesystem.domain.content.adhistory.AdHistory, com.example.balancesystem.domain.content.adhistory.QAdHistory>createList("adHistories", com.example.balancesystem.domain.content.adhistory.AdHistory.class, com.example.balancesystem.domain.content.adhistory.QAdHistory.class, PathInits.DIRECT2);

    public final NumberPath<Integer> adViewCount = createNumber("adViewCount", Integer.class);

    public final NumberPath<Integer> duration = createNumber("duration", Integer.class);

    public final NumberPath<Long> ownerId = createNumber("ownerId", Long.class);

    public final ListPath<com.example.balancesystem.domain.content.videohistory.PlayHistory, com.example.balancesystem.domain.content.videohistory.QPlayHistory> playHistories = this.<com.example.balancesystem.domain.content.videohistory.PlayHistory, com.example.balancesystem.domain.content.videohistory.QPlayHistory>createList("playHistories", com.example.balancesystem.domain.content.videohistory.PlayHistory.class, com.example.balancesystem.domain.content.videohistory.QPlayHistory.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final NumberPath<Long> videoId = createNumber("videoId", Long.class);

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QVideo(String variable) {
        super(Video.class, forVariable(variable));
    }

    public QVideo(Path<? extends Video> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVideo(PathMetadata metadata) {
        super(Video.class, metadata);
    }

}

