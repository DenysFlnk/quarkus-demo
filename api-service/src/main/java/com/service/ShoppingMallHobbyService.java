package com.service;

import com.mapper.ShoppingMallMapper;
import com.quarkus.model.Hobby;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import shopping_mall_hobby.ShoppingMallHobbyProtoService;

@ApplicationScoped
@RequiredArgsConstructor
public class ShoppingMallHobbyService {

    private final ShoppingMallMapper mallMapper;

    @GrpcClient("shopping-mall-hobby-service")
    ShoppingMallHobbyProtoService mallHobbyProtoService;

    @CacheInvalidate(cacheName = "shoppingMallCache")
    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<Void> addHobbyToShoppingMall(@CacheKey Integer mallId, Hobby hobby, String author) {
        return mallHobbyProtoService.addHobby(mallMapper.toShoppingMallHobbyCreateRequest(mallId, hobby, author))
            .replaceWithVoid();
    }

    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<Void> deleteHobbyFromShoppingMall(Integer mallHobbyId, String author) {
        return mallHobbyProtoService.deleteHobby(mallMapper.toShoppingMallHobbyDeleteRequest(mallHobbyId, author))
            .replaceWithVoid();
    }

    @CacheInvalidateAll(cacheName = "shoppingMallListCache")
    public Uni<Void> restoreHobbyInShoppingMall(Integer mallHobbyId, String author) {
        return mallHobbyProtoService.restoreHobby(mallMapper.toShoppingMallHobbyRestoreRequest(mallHobbyId, author))
            .replaceWithVoid();
    }
}
