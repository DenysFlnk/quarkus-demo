package com.mapper;

import com.google.protobuf.Timestamp;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.mapstruct.Mapper;
import shopping_mall.Location;

@Mapper
public interface BaseProtoMapper {

    default Timestamp toTimestamp(LocalDate localDate) {
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).build();
    }

    default LocalDate toLocalDate(Timestamp timestamp) {
        Instant date = Instant.ofEpochSecond(timestamp.getSeconds());
        return LocalDate.ofInstant(date, ZoneId.systemDefault());
    }

    default Point toPoint(Location location) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(location.getLat(), location.getLng()));
    }

    default Location toLocation(Point point) {
        return Location.newBuilder()
            .setLat(point.getX())
            .setLng(point.getY())
            .build();
    }
}
