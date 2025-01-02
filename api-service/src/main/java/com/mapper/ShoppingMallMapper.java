package com.mapper;

import com.quarkus.model.ShoppingMall;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.quarkus.model.UpdateShoppingMallStatusRequest;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import org.mapstruct.factory.Mappers;
import shopping_mall.OperationalStatus;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;
import shopping_mall.UpdateStatusRequest;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface ShoppingMallMapper {

    ShoppingMallMapper INSTANCE = Mappers.getMapper(ShoppingMallMapper.class);

    default List<ShoppingMall> toShoppingMallList(ShoppingMallList shoppingMallList) {
        return shoppingMallList.getMallsList().stream()
            .map(INSTANCE::toShoppingMall)
            .toList();
    }

    default UpdateStatusRequest toUpdateStatusRequest(Integer mallId, UpdateShoppingMallStatusRequest request) {
        return UpdateStatusRequest.newBuilder()
            .setMallId(mallId)
            .setStatus(INSTANCE.toOperationalStatus(request.getOperationalStatus()))
            .build();
    }

    @ValueMappings({
        @ValueMapping(target = "UNRECOGNIZED", source = "OPERATIONAL_STATUS_UNSPECIFIED"),
        @ValueMapping(target = "OPEN", source = "OPERATIONAL_STATUS_OPEN"),
        @ValueMapping(target = "CLOSED", source = "OPERATIONAL_STATUS_CLOSED"),
        @ValueMapping(target = "UNDER_CONSTRUCTION", source = "OPERATIONAL_STATUS_UNDER_CONSTRUCTION"),
        @ValueMapping(target = "RENOVATION", source = "OPERATIONAL_STATUS_RENOVATION")
    })
    com.quarkus.model.OperationalStatus toOperationalStatus(OperationalStatus operationalStatus);

    @ValueMappings({
        @ValueMapping(target = "OPERATIONAL_STATUS_UNSPECIFIED", source = "UNRECOGNIZED"),
        @ValueMapping(target = "OPERATIONAL_STATUS_OPEN", source = "OPEN"),
        @ValueMapping(target = "OPERATIONAL_STATUS_CLOSED", source = "CLOSED"),
        @ValueMapping(target = "OPERATIONAL_STATUS_UNDER_CONSTRUCTION", source = "UNDER_CONSTRUCTION"),
        @ValueMapping(target = "OPERATIONAL_STATUS_RENOVATION", source = "RENOVATION")
    })
    OperationalStatus toOperationalStatus(com.quarkus.model.OperationalStatus operationalStatus);

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
