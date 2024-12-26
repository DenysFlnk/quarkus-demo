package com.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.mapper.ShoppingMallMapper;
import com.messaging.producer.ShoppingMallNotificationProducer;
import com.quarkus.model.AlertToPersonList;
import com.quarkus.model.ShoppingMall;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.quarkus.model.UpdateShoppingMallStatusRequest;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shopping_mall.ShoppingMallProtoService;

@ApplicationScoped
@RequiredArgsConstructor
public class ShoppingMallService {

    private static final ShoppingMallMapper MALL_MAPPER = ShoppingMallMapper.INSTANCE;

    private final ShoppingMallNotificationProducer notificationProducer;

    @GrpcClient("shopping-mall-service")
    ShoppingMallProtoService shoppingMallProtoService;

    @CacheResult(cacheName = "shoppingMallListCache")
    public Uni<List<ShoppingMall>> getAllShoppingMalls() {
        return shoppingMallProtoService.getAllMalls(Empty.getDefaultInstance()).map(MALL_MAPPER::toShoppingMallList);
    }

    @CacheResult(cacheName = "shoppingMallCache")
    public Uni<ShoppingMall> getShoppingMallById(@CacheKey Integer id) {
        return shoppingMallProtoService.getMall(Int32Value.of(id)).map(MALL_MAPPER::toShoppingMall);
    }

    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<ShoppingMall> createShoppingMall(ShoppingMallCreateRequest shoppingMall) {
        return shoppingMallProtoService.createMall(MALL_MAPPER.toShoppingMallObject(shoppingMall))
            .map(MALL_MAPPER::toShoppingMall);
    }

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<Void> updateShoppingMall(@CacheKey Integer id, ShoppingMall shoppingMall) {
        shoppingMall.setId(id);
        return shoppingMallProtoService.updateMall(MALL_MAPPER.toShoppingMallObject(shoppingMall))
            .replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<Void> updateShoppingMallStatus(@CacheKey Integer id, UpdateShoppingMallStatusRequest request) {
        return shoppingMallProtoService.updateMallStatus(MALL_MAPPER.toUpdateStatusRequest(id, request))
            .replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<Void> deleteShoppingMall(@CacheKey Integer id) {
        return shoppingMallProtoService.deleteMall(Int32Value.of(id)).replaceWithVoid();
    }

    public Uni<Void> sendAlertToPersonList(AlertToPersonList alertToPersonList) {
        if (alertToPersonList.getIdList().isEmpty() || alertToPersonList.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Person id list or message is empty");
        }

        return notificationProducer.buildAndPublish(alertToPersonList.getIdList(), alertToPersonList.getMessage());
    }
}
