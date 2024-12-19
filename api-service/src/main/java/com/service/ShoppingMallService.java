package com.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.mapper.ShoppingMallMapper;
import com.person.model.ShoppingMall;
import com.person.model.ShoppingMallCreateRequest;
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

    private final ShoppingMallMapper mallMapper;

    @GrpcClient("shopping-mall-service")
    ShoppingMallProtoService shoppingMallProtoService;

    @CacheResult(cacheName = "shoppingMallListCache")
    public Uni<List<ShoppingMall>> getAllShoppingMalls() {
        return shoppingMallProtoService.getAllMalls(Empty.getDefaultInstance()).map(mallMapper::toShoppingMallList);
    }

    @CacheResult(cacheName = "shoppingMallCache")
    public Uni<ShoppingMall> getShoppingMallById(@CacheKey Integer id) {
        return shoppingMallProtoService.getMall(Int32Value.of(id)).map(mallMapper::toShoppingMall);
    }

    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<ShoppingMall> createShoppingMall(ShoppingMallCreateRequest shoppingMall) {
        return shoppingMallProtoService.createMall(mallMapper.toShoppingMallObject(shoppingMall))
            .map(mallMapper::toShoppingMall);
    }

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<Void> updateShoppingMall(@CacheKey Integer id, ShoppingMall shoppingMall) {
        shoppingMall.setId(id);
        return shoppingMallProtoService.updateMall(mallMapper.toShoppingMallObject(shoppingMall))
            .replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<Void> deleteShoppingMall(@CacheKey Integer id) {
        return shoppingMallProtoService.deleteMall(Int32Value.of(id)).replaceWithVoid();
    }
}
