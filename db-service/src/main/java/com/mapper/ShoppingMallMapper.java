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

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE, uses = BaseProtoMapper.class)
public interface ShoppingMallMapper {

    ShoppingMallMapper INSTANCE = Mappers.getMapper(ShoppingMallMapper.class);

    default ShoppingMallList toShoppingMallList(List<ShoppingMall> malls) {
        return ShoppingMallList.newBuilder()
            .addAllMalls(malls.stream()
                .map(INSTANCE::toShoppingMallObject)
                .toList())
            .build();
    }

    @Mapping(target = "hobbiesList", source = "hobbies")
    ShoppingMallObject toShoppingMallObject(ShoppingMall mall);

    @Mapping(target = "hobbies", source = "hobbiesList")
    @Mapping(target = "id", ignore = true)
    ShoppingMall toShoppingMall(ShoppingMallObject shoppingMallObject);

    @Mapping(target = "hobbies", source = "hobbiesList")
    void updateShoppingMall(@MappingTarget ShoppingMall mall, ShoppingMallObject shoppingMallObject);
}
