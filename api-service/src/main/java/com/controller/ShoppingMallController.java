package com.controller;

import com.quarkus.api.ShoppingMallControllerApi;
import com.quarkus.model.AlertToPersonList;
import com.quarkus.model.ShoppingMall;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.quarkus.model.ShoppingMallUpdateRequest;
import com.service.MallRestrictionService;
import com.service.ShoppingMallService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Authenticated
public class ShoppingMallController implements ShoppingMallControllerApi {

    @Inject
    ShoppingMallService shoppingMallService;

    @Inject
    MallRestrictionService restrictionService;

    @Override
    public Uni<List<ShoppingMall>> getAllShoppingMalls() {
        List<Integer> restrictedMalls = restrictionService.getRestrictedMalls();

        if (restrictedMalls.isEmpty()) {
            return shoppingMallService.getAllShoppingMalls();
        } else {
            return shoppingMallService.getRestrictedShoppingMalls(restrictedMalls);
        }
    }

    @Override
    public Uni<ShoppingMall> getShoppingMall(Integer id) {
        if (restrictionService.isRestrictedMall(id)) {
            throw new SecurityException("Shopping mall is restricted");
        }

        return shoppingMallService.getShoppingMallById(id);
    }

    @Override
    public Uni<ShoppingMall> createShoppingMall(ShoppingMallCreateRequest shoppingMallCreateRequest) {
        return shoppingMallService.createShoppingMall(shoppingMallCreateRequest);
    }

    @Override
    public Uni<Void> deleteShoppingMall(Integer id) {
        if (restrictionService.isRestrictedMall(id)) {
            throw new SecurityException("Shopping mall is restricted");
        }

        return shoppingMallService.deleteShoppingMall(id);
    }

    @Override
    public Uni<Void> updateShoppingMall(Integer id, ShoppingMallUpdateRequest shoppingMallUpdateRequest) {
        if (restrictionService.isRestrictedMall(id)) {
            throw new SecurityException("Shopping mall is restricted");
        }

        return shoppingMallService.updateShoppingMall(id, shoppingMallUpdateRequest);
    }

    @Override
    public Uni<Void> sendAlertToPersonList(AlertToPersonList alertToPersonList) {
        return shoppingMallService.sendAlertToPersonList(alertToPersonList);
    }

    @Override
    public Uni<Response> downloadShoppingMallsListXlsx() {
        return shoppingMallService.getShoppingMallListXlsxFile()
            .map(file -> Response.ok(file)
                .header("Content-Disposition", "attachment; filename=shopping_mall_list.xlsx")
                .build());
    }

    @Override
    public Uni<Response> downloadShoppingMallsListDocx() {
        return shoppingMallService.getShoppingMallListDocxFile()
            .map(file -> Response.ok(file)
                .header("Content-Disposition", "attachment; filename=shopping_mall_list.docx")
                .build());
    }
}
