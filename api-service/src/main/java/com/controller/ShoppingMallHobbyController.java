package com.controller;

import com.quarkus.api.ShoppingMallHobbyControllerApi;
import com.quarkus.model.Hobby;
import com.service.MallRestrictionService;
import com.service.ShoppingMallHobbyService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@Authenticated
public class ShoppingMallHobbyController implements ShoppingMallHobbyControllerApi {

    @Inject
    ShoppingMallHobbyService shoppingMallHobbyService;

    @Inject
    MallRestrictionService restrictionService;

    @Override
    public Uni<Void> addHobbyToShoppingMall(Integer mallId, Hobby hobby) {
        return shoppingMallHobbyService.addHobbyToShoppingMall(mallId, hobby, restrictionService.getRequestAuthor());
    }

    @Override
    public Uni<Void> deleteShoppingMallHobby(Integer shoppingMallHobbyId) {
        return shoppingMallHobbyService.deleteHobbyFromShoppingMall(shoppingMallHobbyId,
            restrictionService.getRequestAuthor());
    }
}
