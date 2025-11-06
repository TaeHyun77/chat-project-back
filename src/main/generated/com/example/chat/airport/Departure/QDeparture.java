package com.example.chat.airport.Departure;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeparture is a Querydsl query type for Departure
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeparture extends EntityPathBase<Departure> {

    private static final long serialVersionUID = -104507694L;

    public static final QDeparture departure = new QDeparture("departure");

    public final com.example.chat.config.QBaseTime _super = new com.example.chat.config.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath date = createString("date");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> t1Depart12 = createNumber("t1Depart12", Long.class);

    public final NumberPath<Long> t1Depart3 = createNumber("t1Depart3", Long.class);

    public final NumberPath<Long> t1Depart4 = createNumber("t1Depart4", Long.class);

    public final NumberPath<Long> t1Depart56 = createNumber("t1Depart56", Long.class);

    public final NumberPath<Long> t1DepartSum = createNumber("t1DepartSum", Long.class);

    public final NumberPath<Long> t2Depart1 = createNumber("t2Depart1", Long.class);

    public final NumberPath<Long> t2Depart2 = createNumber("t2Depart2", Long.class);

    public final NumberPath<Long> t2DepartSum = createNumber("t2DepartSum", Long.class);

    public final StringPath timeZone = createString("timeZone");

    public QDeparture(String variable) {
        super(Departure.class, forVariable(variable));
    }

    public QDeparture(Path<? extends Departure> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeparture(PathMetadata metadata) {
        super(Departure.class, metadata);
    }

}

