package com.mapper;

import com.entity.ShoppingMall;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import shopping_mall.Location;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface ShoppingMallMapper {

    ShoppingMallMapper INSTANCE = Mappers.getMapper(ShoppingMallMapper.class);

    default ShoppingMallList toShoppingMallList(List<ShoppingMall> malls) {
        return ShoppingMallList.newBuilder()
            .addAllMalls(malls.stream()
                .map(INSTANCE::toShoppingMallObject)
                .toList())
            .build();
    }

    @Named("pointToLocation")
    default Location toLocation(Point point) {
        return Location.newBuilder()
            .setLat(point.getX())
            .setLng(point.getY())
            .build();
    }

    @Named("locationToPoint")
    default Point toPoint(Location location) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(location.getLat(), location.getLng()));
    }

    @Mapping(target = "location" , source = "location", qualifiedByName = "pointToLocation")
    @Mapping(target = "hobbiesList", source = "hobbies")
    ShoppingMallObject toShoppingMallObject(ShoppingMall mall);

    @Mapping(target = "location" , source = "location", qualifiedByName = "locationToPoint")
    @Mapping(target = "hobbies", source = "hobbiesList")
    @Mapping(target = "id", ignore = true)
    ShoppingMall toShoppingMall(ShoppingMallObject shoppingMallObject);

    @Mapping(target = "location" , source = "location", qualifiedByName = "locationToPoint")
    @Mapping(target = "hobbies", source = "hobbiesList")
    void updateShoppingMall(@MappingTarget ShoppingMall mall, ShoppingMallObject shoppingMallObject);
}
