package com.example.chat.airport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlane is a Querydsl query type for Plane
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlane extends EntityPathBase<Plane> {

    private static final long serialVersionUID = -834628391L;

    public static final QPlane plane = new QPlane("plane");

    public final com.example.chat.config.QBaseTime _super = new com.example.chat.config.QBaseTime(this);

    public final StringPath aircraftRegNo = createString("aircraftRegNo");

    public final StringPath airLine = createString("airLine");

    public final StringPath airport = createString("airport");

    public final StringPath airportCode = createString("airportCode");

    public final StringPath codeShare = createString("codeShare");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath estimatedDatetime = createString("estimatedDatetime");

    public final StringPath flightId = createString("flightId");

    public final StringPath gateNumber = createString("gateNumber");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath remark = createString("remark");

    public final StringPath scheduleDatetime = createString("scheduleDatetime");

    public final StringPath searchDate = createString("searchDate");

    public final StringPath terminalId = createString("terminalId");

    public QPlane(String variable) {
        super(Plane.class, forVariable(variable));
    }

    public QPlane(Path<? extends Plane> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlane(PathMetadata metadata) {
        super(Plane.class, metadata);
    }

}

