package com.example.balancesystem.global.revenuerate;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRevenueRate is a Querydsl query type for RevenueRate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRevenueRate extends EntityPathBase<RevenueRate> {

    private static final long serialVersionUID = -1196352429L;

    public static final QRevenueRate revenueRate = new QRevenueRate("revenueRate");

    public final NumberPath<Long> maxViews = createNumber("maxViews", Long.class);

    public final NumberPath<Long> minViews = createNumber("minViews", Long.class);

    public final NumberPath<Double> rate = createNumber("rate", Double.class);

    public final NumberPath<Long> revenueRateId = createNumber("revenueRateId", Long.class);

    public final EnumPath<RevenueType> type = createEnum("type", RevenueType.class);

    public QRevenueRate(String variable) {
        super(RevenueRate.class, forVariable(variable));
    }

    public QRevenueRate(Path<? extends RevenueRate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRevenueRate(PathMetadata metadata) {
        super(RevenueRate.class, metadata);
    }

}

