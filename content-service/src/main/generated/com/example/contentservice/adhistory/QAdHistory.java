package com.example.contentservice.adhistory;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdHistory is a Querydsl query type for AdHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdHistory extends EntityPathBase<AdHistory> {

    private static final long serialVersionUID = 578666739L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdHistory adHistory = new QAdHistory("adHistory");

    public final com.example.contentservice.ad.QAd ad;

    public final NumberPath<Long> adHistoryId = createNumber("adHistoryId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.example.contentservice.video.QVideo video;

    public final DatePath<java.time.LocalDate> viewDate = createDate("viewDate", java.time.LocalDate.class);

    public QAdHistory(String variable) {
        this(AdHistory.class, forVariable(variable), INITS);
    }

    public QAdHistory(Path<? extends AdHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdHistory(PathMetadata metadata, PathInits inits) {
        this(AdHistory.class, metadata, inits);
    }

    public QAdHistory(Class<? extends AdHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ad = inits.isInitialized("ad") ? new com.example.contentservice.ad.QAd(forProperty("ad")) : null;
        this.video = inits.isInitialized("video") ? new com.example.contentservice.video.QVideo(forProperty("video")) : null;
    }

}

