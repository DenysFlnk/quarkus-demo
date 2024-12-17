package com.controller;

import com.person.api.ShoppingMallControllerApi;
import com.person.model.ShoppingMall;
import com.person.model.ShoppingMallCreateRequest;
import com.service.ShoppingMallService;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class ShoppingMallController implements ShoppingMallControllerApi {

    @Inject
    ShoppingMallService shoppingMallService;

    @Override
    public CompletionStage<List<ShoppingMall>> getAllShoppingMalls() {
        return shoppingMallService.getAllShoppingMalls().subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<ShoppingMall> getShoppingMall(Integer id) {
        return shoppingMallService.getShoppingMallById(id).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<ShoppingMall> createShoppingMall(ShoppingMallCreateRequest shoppingMallCreateRequest) {
        return shoppingMallService.createShoppingMall(shoppingMallCreateRequest).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<Void> deleteShoppingMall(Integer id) {
        return shoppingMallService.deleteShoppingMall(id).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<Void> updateShoppingMall(Integer id, ShoppingMall shoppingMall) {
        return shoppingMallService.updateShoppingMall(id, shoppingMall).subscribeAsCompletionStage();
    }
}
