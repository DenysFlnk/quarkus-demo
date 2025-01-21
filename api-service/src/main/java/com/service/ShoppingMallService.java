package com.service;

import com.google.protobuf.BoolValue;
import com.mapper.ShoppingMallMapper;
import com.messaging.producer.ShoppingMallNotificationProducer;
import com.quarkus.model.AlertToPersonList;
import com.quarkus.model.ShoppingMall;
import com.quarkus.model.ShoppingMallCreateRequest;
import com.quarkus.model.ShoppingMallUpdateRequest;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shopping_mall.ShoppingMallProtoService;

@ApplicationScoped
@RequiredArgsConstructor
public class ShoppingMallService {

    private final ShoppingMallMapper mallMapper;

    private final ShoppingMallNotificationProducer notificationProducer;

    private final DocumentService documentService;

    @GrpcClient("shopping-mall-service")
    ShoppingMallProtoService shoppingMallProtoService;

    @CacheResult(cacheName = "shoppingMallListCache")
    public Uni<List<ShoppingMall>> getAllShoppingMalls(Boolean includeDeleted) {
        return shoppingMallProtoService.getAllMalls(BoolValue.of(includeDeleted)).map(mallMapper::toShoppingMallList);
    }

    @CacheResult(cacheName = "restrictedShoppingMallListCache")
    public Uni<List<ShoppingMall>> getRestrictedShoppingMalls(List<Integer> restrictedIds, Boolean includeDeleted) {
        return shoppingMallProtoService.getMallsWithoutRestricted(mallMapper.toRestrictedMallIds(restrictedIds,
                includeDeleted))
            .map(mallMapper::toShoppingMallList);
    }

    @CacheResult(cacheName = "shoppingMallCache")
    public Uni<ShoppingMall> getShoppingMallById(Integer id, Boolean includeDeleted) {
        return shoppingMallProtoService.getMall(mallMapper.toGetMallRequest(id, includeDeleted))
            .map(mallMapper::toShoppingMall);
    }

    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<ShoppingMall> createShoppingMall(ShoppingMallCreateRequest shoppingMall, String author) {
        return shoppingMallProtoService.createMall(mallMapper.toShoppingMallObject(shoppingMall, author))
            .map(mallMapper::toShoppingMall);
    }

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    @CacheInvalidateAll(cacheName = "restrictedShoppingMallListCache")
    public Uni<Void> updateShoppingMall(@CacheKey Integer id, ShoppingMallUpdateRequest shoppingMall, String author) {
        return shoppingMallProtoService.getMall(mallMapper.toGetMallRequest(id, false))
            .flatMap(mall ->
                shoppingMallProtoService.updateMall(mallMapper.updateAndBuildShoppingMall(mall, shoppingMall, author)))
            .replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    @CacheInvalidateAll(cacheName = "restrictedShoppingMallListCache")
    public Uni<Void> deleteShoppingMall(@CacheKey Integer id, String author) {
        return shoppingMallProtoService.deleteMall(mallMapper.toDeleteMallRequest(id, author)).replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    @CacheInvalidateAll(cacheName = "restrictedShoppingMallListCache")
    public Uni<Void> restoreShoppingMall(@CacheKey Integer id, String author) {
        return shoppingMallProtoService.restoreMall(mallMapper.toRestoreMallRequest(id, author)).replaceWithVoid();
    }

    public Uni<Void> sendAlertToPersonList(AlertToPersonList alertToPersonList) {
        if (alertToPersonList.getIdList().isEmpty() || alertToPersonList.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Person id list or message is empty");
        }

        return notificationProducer.buildAndPublish(alertToPersonList.getIdList(), alertToPersonList.getMessage());
    }

    public Uni<File> getShoppingMallListXlsxFile(Boolean includeDeleted) {
        return getAllShoppingMalls(includeDeleted).map(documentService::convertShoppingMallListToXlsx);
    }

    public Uni<File> getShoppingMallListDocxFile(Boolean includeDeleted) {
        return getAllShoppingMalls(includeDeleted).map(documentService::convertShoppingMallListToDocx);
    }
}
