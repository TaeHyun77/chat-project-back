package com.example.chat.common;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseBatch is a Querydsl query type for BaseBatch
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseBatch extends EntityPathBase<BaseBatch> {

    private static final long serialVersionUID = -1517999829L;

    public static final QBaseBatch baseBatch = new QBaseBatch("baseBatch");

    public final QBaseTime _super = new QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public QBaseBatch(String variable) {
        super(BaseBatch.class, forVariable(variable));
    }

    public QBaseBatch(Path<? extends BaseBatch> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseBatch(PathMetadata metadata) {
        super(BaseBatch.class, metadata);
    }

}

