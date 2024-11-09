package com.example.balancesystem.domain.content.videohistory;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlayHistory is a Querydsl query type for PlayHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlayHistory extends EntityPathBase<PlayHistory> {

    private static final long serialVersionUID = -1639749738L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlayHistory playHistory = new QPlayHistory("playHistory");

    public final NumberPath<Long> historyId = createNumber("historyId", Long.class);

    public final BooleanPath isCompleted = createBoolean("isCompleted");

    public final NumberPath<Integer> lastPlayedAt = createNumber("lastPlayedAt", Integer.class);

    public final NumberPath<Integer> playTime = createNumber("playTime", Integer.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.example.balancesystem.domain.content.video.QVideo video;

    public final DateTimePath<java.time.LocalDateTime> viewDate = createDateTime("viewDate", java.time.LocalDateTime.class);

    public QPlayHistory(String variable) {
        this(PlayHistory.class, forVariable(variable), INITS);
    }

    public QPlayHistory(Path<? extends PlayHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlayHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlayHistory(PathMetadata metadata, PathInits inits) {
        this(PlayHistory.class, metadata, inits);
    }

    public QPlayHistory(Class<? extends PlayHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.video = inits.isInitialized("video") ? new com.example.balancesystem.domain.content.video.QVideo(forProperty("video")) : null;
    }

}

