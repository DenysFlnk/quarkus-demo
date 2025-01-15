package com.mapper;

import com.entity.OperationalStatus;
import com.entity.ShoppingMall;
import com.entity.ShoppingMallHobby;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import shopping_mall.RestrictedMallIds;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;
import shopping_mall_hobby.ShoppingMallHobbyCreateRequest;

@Mapper(
    uses = BaseProtoMapper.class,
    collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    componentModel = MappingConstants.ComponentModel.CDI)
public interface ShoppingMallMapper {

    default ShoppingMallList toShoppingMallList(List<ShoppingMall> malls) {
        return ShoppingMallList.newBuilder()
            .addAllMalls(malls.stream()
                .map(this::toShoppingMallObject)
                .toList())
            .build();
    }

    default List<Integer> toIntegerList(RestrictedMallIds mallIds) {
        return mallIds.getIdsList();
    }

    @ValueMappings({
        @ValueMapping(target = "UNRECOGNIZED", source = "OPERATIONAL_STATUS_UNSPECIFIED"),
        @ValueMapping(target = "OPEN", source = "OPERATIONAL_STATUS_OPEN"),
        @ValueMapping(target = "CLOSED", source = "OPERATIONAL_STATUS_CLOSED"),
        @ValueMapping(target = "UNDER_CONSTRUCTION", source = "OPERATIONAL_STATUS_UNDER_CONSTRUCTION"),
        @ValueMapping(target = "RENOVATION", source = "OPERATIONAL_STATUS_RENOVATION")
    })
    OperationalStatus toOperationalStatus(shopping_mall.OperationalStatus status);

    @ValueMappings({
        @ValueMapping(target = "OPERATIONAL_STATUS_UNSPECIFIED", source = "UNRECOGNIZED"),
        @ValueMapping(target = "OPERATIONAL_STATUS_OPEN", source = "OPEN"),
        @ValueMapping(target = "OPERATIONAL_STATUS_CLOSED", source = "CLOSED"),
        @ValueMapping(target = "OPERATIONAL_STATUS_UNDER_CONSTRUCTION", source = "UNDER_CONSTRUCTION"),
        @ValueMapping(target = "OPERATIONAL_STATUS_RENOVATION", source = "RENOVATION")
    })
    shopping_mall.OperationalStatus toOperationalStatus(OperationalStatus status);

    @Mapping(target = "hobbiesList", source = "hobbies")
    @Mapping(target = "status", source = "operationalStatus")
    @Mapping(target = "location", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ShoppingMallObject toShoppingMallObject(ShoppingMall mall);

    @Mapping(target = "hobbies", source = "hobbiesList")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operationalStatus", source = "status")
    ShoppingMall toShoppingMall(ShoppingMallObject shoppingMallObject);

    @Mapping(target = "operationalStatus", source = "status")
    void updateShoppingMall(@MappingTarget ShoppingMall mall, ShoppingMallObject shoppingMallObject);

    @Mapping(target = "id", ignore = true)
    ShoppingMallHobby toShoppingMallHobby(ShoppingMallHobbyCreateRequest request);
}
