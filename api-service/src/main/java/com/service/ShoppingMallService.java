package com.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
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
    public Uni<List<ShoppingMall>> getAllShoppingMalls() {
        return shoppingMallProtoService.getAllMalls(Empty.getDefaultInstance()).map(mallMapper::toShoppingMallList);
    }

    @CacheResult(cacheName = "restrictedShoppingMallListCache")
    public Uni<List<ShoppingMall>> getRestrictedShoppingMalls(List<Integer> restrictedIds) {
        return shoppingMallProtoService.getMallsWithoutRestricted(mallMapper.toRestrictedMallIds(restrictedIds))
            .map(mallMapper::toShoppingMallList);
    }

    @CacheResult(cacheName = "shoppingMallCache")
    public Uni<ShoppingMall> getShoppingMallById(Integer id) {
        return shoppingMallProtoService.getMall(Int32Value.of(id)).map(mallMapper::toShoppingMall);
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
        return shoppingMallProtoService.getMall(Int32Value.of(id))
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

    public Uni<Void> sendAlertToPersonList(AlertToPersonList alertToPersonList) {
        if (alertToPersonList.getIdList().isEmpty() || alertToPersonList.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Person id list or message is empty");
        }

        return notificationProducer.buildAndPublish(alertToPersonList.getIdList(), alertToPersonList.getMessage());
    }

    public Uni<File> getShoppingMallListXlsxFile() {
        return getAllShoppingMalls().map(documentService::convertShoppingMallListToXlsx);
    }

    public Uni<File> getShoppingMallListDocxFile() {
        return getAllShoppingMalls().map(documentService::convertShoppingMallListToDocx);
    }
}
