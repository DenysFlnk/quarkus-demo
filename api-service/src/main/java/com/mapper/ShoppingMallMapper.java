package com.mapper;

import com.quarkus.model.Hobby;
import com.quarkus.model.ShoppingMall;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.quarkus.model.ShoppingMallUpdateRequest;
import com.web_socket.dto.ShoppingMallUpdateRequestDto;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import shopping_mall.DeleteMallRequest;
import shopping_mall.GetMallRequest;
import shopping_mall.OperationalStatus;
import shopping_mall.RestoreMallRequest;
import shopping_mall.RestrictedMallIds;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;
import shopping_mall_hobby.ShoppingMallHobbyCreateRequest;
import shopping_mall_hobby.ShoppingMallHobbyDeleteRequest;
import shopping_mall_hobby.ShoppingMallHobbyRestoreRequest;

@Mapper(
    uses = {JsonNullableMapper.class, LocationMapper.class, HobbyMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    componentModel = MappingConstants.ComponentModel.JAKARTA_CDI
)
public interface ShoppingMallMapper {

    default List<ShoppingMall> toShoppingMallList(ShoppingMallList shoppingMallList) {
        return shoppingMallList.getMallsList().stream()
            .map(this::toShoppingMall)
            .toList();
    }

    default ShoppingMallObject updateAndBuildShoppingMall(ShoppingMallObject mall, ShoppingMallUpdateRequest request,
                                                          String author) {
        ShoppingMallObject.Builder builder = mall.toBuilder();
        updateShoppingMall(builder, request, author);
        return builder.build();
    }

    default RestrictedMallIds toRestrictedMallIds(List<Integer> restrictedMallIds, Boolean includeDeleted) {
        return RestrictedMallIds.newBuilder()
            .addAllIds(restrictedMallIds)
            .setIncludeDeleted(includeDeleted)
            .build();
    }

    @AfterMapping
    default void setLocation(@MappingTarget ShoppingMallObject.Builder mall, ShoppingMallUpdateRequest request) {
        final var jsonNullableMapper = JsonNullableMapper.INSTANCE;
        final var locationMapper = LocationMapper.INSTANCE;

        if (jsonNullableMapper.isNotEmpty(request.getLocation())) {
            mall.setLocation(locationMapper.toLocation(request.getLocation().get()));
        } else if (request.getLocation() == null) {
            mall.clearLocation();
        }
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

    @Mapping(target = "name", source = "shoppingMall.name")
    @Mapping(target = "location", source = "shoppingMall.location")
    @Mapping(target = "status", source = "shoppingMall.operationalStatus")
    @Mapping(target = "author", source = "author")
    ShoppingMallObject toShoppingMallObject(ShoppingMallCreateRequest shoppingMall, String author);

    ShoppingMallUpdateRequest toShoppingMallUpdateRequest(ShoppingMallUpdateRequestDto dto);

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "hobbiesList", ignore = true)
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "author", source = "author")
    void updateShoppingMall(@MappingTarget ShoppingMallObject.Builder mall, ShoppingMallUpdateRequest request,
                            String author);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "author", source = "author")
    DeleteMallRequest toDeleteMallRequest(Integer id, String author);

    ShoppingMallHobbyCreateRequest toShoppingMallHobbyCreateRequest(Integer mallId, Hobby hobby, String author);

    ShoppingMallHobbyDeleteRequest toShoppingMallHobbyDeleteRequest(Integer shoppingMallHobbyId, String author);

    GetMallRequest toGetMallRequest(Integer id, Boolean includeDeleted);

    RestoreMallRequest toRestoreMallRequest(Integer id, String author);

    ShoppingMallHobbyRestoreRequest toShoppingMallHobbyRestoreRequest(Integer shoppingMallHobbyId, String author);
}
