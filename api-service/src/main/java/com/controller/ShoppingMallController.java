package com.controller;

import com.quarkus.api.ShoppingMallControllerApi;
import com.quarkus.model.ShoppingMall;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.quarkus.model.UpdateShoppingMallStatusRequest;
import com.service.ShoppingMallService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

//@Authenticated
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

    @Override
    public CompletionStage<Void> updateShoppingMallStatus(Integer id,
                                                          UpdateShoppingMallStatusRequest updateStatusRequest) {
        return shoppingMallService.updateShoppingMallStatus(id, updateStatusRequest).subscribeAsCompletionStage();
    }
}
