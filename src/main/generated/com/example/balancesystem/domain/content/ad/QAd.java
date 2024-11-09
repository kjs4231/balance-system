package com.example.balancesystem.domain.content.ad;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAd is a Querydsl query type for Ad
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAd extends EntityPathBase<Ad> {

    private static final long serialVersionUID = -1520440285L;

    public static final QAd ad = new QAd("ad");

    public final NumberPath<Long> adId = createNumber("adId", Long.class);

    public QAd(String variable) {
        super(Ad.class, forVariable(variable));
    }

    public QAd(Path<? extends Ad> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAd(PathMetadata metadata) {
        super(Ad.class, metadata);
    }

}

