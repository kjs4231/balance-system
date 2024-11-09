package com.example.balancesystem.global.videorevenue;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVideoRevenue is a Querydsl query type for VideoRevenue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVideoRevenue extends EntityPathBase<VideoRevenue> {

    private static final long serialVersionUID = -1396303913L;

    public static final QVideoRevenue videoRevenue = new QVideoRevenue("videoRevenue");

    public final NumberPath<java.math.BigDecimal> adRevenue = createNumber("adRevenue", java.math.BigDecimal.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> revenueId = createNumber("revenueId", Long.class);

    public final NumberPath<java.math.BigDecimal> totalRevenue = createNumber("totalRevenue", java.math.BigDecimal.class);

    public final NumberPath<Long> videoId = createNumber("videoId", Long.class);

    public final NumberPath<java.math.BigDecimal> viewRevenue = createNumber("viewRevenue", java.math.BigDecimal.class);

    public QVideoRevenue(String variable) {
        super(VideoRevenue.class, forVariable(variable));
    }

    public QVideoRevenue(Path<? extends VideoRevenue> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVideoRevenue(PathMetadata metadata) {
        super(VideoRevenue.class, metadata);
    }

}

