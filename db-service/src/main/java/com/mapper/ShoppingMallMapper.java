package com.mapper;

import com.entity.OperationalStatus;
import com.entity.ShoppingMall;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;
import shopping_mall.UpdateStatusRequest;

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

    default OperationalStatus toOperationalStatus(shopping_mall.OperationalStatus status) {
        return OperationalStatus.valueOf(status.name());
    }

    default OperationalStatus toOperationalStatus(UpdateStatusRequest request) {
        return OperationalStatus.valueOf(request.getStatus().name());
    }

    default shopping_mall.OperationalStatus toOperationalStatus(OperationalStatus status) {
        return shopping_mall.OperationalStatus.valueOf(status.name());
    }

    @Mapping(target = "hobbiesList", source = "hobbies")
    @Mapping(target = "status", source = "operationalStatus")
    ShoppingMallObject toShoppingMallObject(ShoppingMall mall);

    @Mapping(target = "hobbies", source = "hobbiesList")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operationalStatus", source = "status")
    ShoppingMall toShoppingMall(ShoppingMallObject shoppingMallObject);

    @Mapping(target = "hobbies", source = "hobbiesList")
    void updateShoppingMall(@MappingTarget ShoppingMall mall, ShoppingMallObject shoppingMallObject);
}
