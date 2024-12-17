package com.mapper;

import com.person.model.ShoppingMall;
import com.person.model.ShoppingMallCreateRequest;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface ShoppingMallMapper {

    ShoppingMallMapper INSTANCE = Mappers.getMapper(ShoppingMallMapper.class);

    default List<ShoppingMall> toShoppingMallList(ShoppingMallList shoppingMallList) {
        return shoppingMallList.getMallsList().stream()
            .map(INSTANCE::toShoppingMall)
            .toList();
    }

    @Mapping(target = "location", source = "location")
    @Mapping(target = "hobbies", source = "hobbiesList")
    ShoppingMall toShoppingMall(ShoppingMallObject shoppingMallObject);

    @Mapping(target = "location", source = "location")
    @Mapping(target = "hobbiesList", source = "hobbies")
    ShoppingMallObject toShoppingMallObject(ShoppingMall shoppingMall);

    @Mapping(target = "location", source = "location")
    @Mapping(target = "hobbiesList", source = "hobbies")
    ShoppingMallObject toShoppingMallObject(ShoppingMallCreateRequest shoppingMall);
}
