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
import shopping_mall.OperationalStatus;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;

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

    default ShoppingMallObject updateAndBuildShoppingMall(ShoppingMallObject mall, ShoppingMallUpdateRequest request) {
        ShoppingMallObject.Builder builder = mall.toBuilder();
        updateShoppingMall(builder, request);
        return builder.build();
    }

    @AfterMapping
    default void setHobbies(@MappingTarget ShoppingMallObject.Builder mall, ShoppingMallUpdateRequest request) {
        final var jsonNullableMapper = JsonNullableMapper.INSTANCE;
        final var hobbyMapper = HobbyMapper.INSTANCE;

        if (jsonNullableMapper.isNotEmpty(request.getHobbies())) {
            List<Hobby> hobbies = request.getHobbies().get();
            mall.addAllHobbies(hobbies.stream()
                .map(hobbyMapper::toHobbyObject)
                .toList());
        } else if (request.getHobbies() == null) {
            mall.clearHobbies();
        }
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

    @Mapping(target = "location", source = "location")
    @Mapping(target = "hobbiesList", source = "hobbies")
    @Mapping(target = "status", source = "operationalStatus")
    ShoppingMallObject toShoppingMallObject(ShoppingMallCreateRequest shoppingMall);

    ShoppingMallUpdateRequest toShoppingMallUpdateRequest(ShoppingMallUpdateRequestDto dto);

    @Mapping(target = "location", ignore = true)
    @Mapping(target = "hobbiesList", ignore = true)
    void updateShoppingMall(@MappingTarget ShoppingMallObject.Builder mall, ShoppingMallUpdateRequest request);
}
