package com.controller;

import com.quarkus.api.ShoppingMallControllerApi;
import com.quarkus.model.AlertToPersonList;
import com.quarkus.model.ShoppingMall;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.quarkus.model.ShoppingMallUpdateRequest;
import com.service.ShoppingMallService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import java.util.List;

@Authenticated
public class ShoppingMallController implements ShoppingMallControllerApi {

    @Inject
    ShoppingMallService shoppingMallService;

    @Override
    public Uni<List<ShoppingMall>> getAllShoppingMalls() {
        return shoppingMallService.getAllShoppingMalls();
    }

    @Override
    public Uni<ShoppingMall> getShoppingMall(Integer id) {
        return shoppingMallService.getShoppingMallById(id);
    }

    @Override
    public Uni<ShoppingMall> createShoppingMall(ShoppingMallCreateRequest shoppingMallCreateRequest) {
        return shoppingMallService.createShoppingMall(shoppingMallCreateRequest);
    }

    @Override
    public Uni<Void> deleteShoppingMall(Integer id) {
        return shoppingMallService.deleteShoppingMall(id);
    }

    @Override
    public Uni<Void> updateShoppingMall(Integer id, ShoppingMallUpdateRequest shoppingMallUpdateRequest) {
        return shoppingMallService.updateShoppingMall(id, shoppingMallUpdateRequest);
    }

    @Override
    public Uni<Void> sendAlertToPersonList(AlertToPersonList alertToPersonList) {
        return shoppingMallService.sendAlertToPersonList(alertToPersonList);
    }
}
