package com.mapper;

import com.quarkus.model.ShoppingMall;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.quarkus.model.UpdateShoppingMallStatusRequest;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import shopping_mall.OperationalStatus;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;
import shopping_mall.UpdateStatusRequest;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface ShoppingMallMapper {

    ShoppingMallMapper INSTANCE = Mappers.getMapper(ShoppingMallMapper.class);

    String OPERATIONAL_STATUS = "OPERATIONAL_STATUS_";

    default List<ShoppingMall> toShoppingMallList(ShoppingMallList shoppingMallList) {
        return shoppingMallList.getMallsList().stream()
            .map(INSTANCE::toShoppingMall)
            .toList();
    }

    default UpdateStatusRequest toUpdateStatusRequest(Integer mallId, UpdateShoppingMallStatusRequest request) {
        return UpdateStatusRequest.newBuilder()
            .setMallId(mallId)
            .setStatus(OperationalStatus.valueOf(request.getOperationalStatus().name()))
            .build();
    }

    default com.quarkus.model.OperationalStatus toOperationalStatus(OperationalStatus operationalStatus) {
        return com.quarkus.model.OperationalStatus.valueOf(operationalStatus.name().replaceAll(OPERATIONAL_STATUS, ""));
    }

    default OperationalStatus toOperationalStatus(com.quarkus.model.OperationalStatus operationalStatus) {
        return OperationalStatus.valueOf(OPERATIONAL_STATUS + operationalStatus.name());
    }

    @Mapping(target = "location", source = "location")
    @Mapping(target = "hobbies", source = "hobbiesList")
    @Mapping(target = "operationalStatus", source = "status")
    ShoppingMall toShoppingMall(ShoppingMallObject shoppingMallObject);

    @Mapping(target = "location", source = "location")
    @Mapping(target = "hobbiesList", source = "hobbies")
    @Mapping(target = "status", source = "operationalStatus")
    ShoppingMallObject toShoppingMallObject(ShoppingMall shoppingMall);

    @Mapping(target = "location", source = "location")
    @Mapping(target = "hobbiesList", source = "hobbies")
    ShoppingMallObject toShoppingMallObject(ShoppingMallCreateRequest shoppingMall);
}
