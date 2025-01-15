package com.service;

import com.mapper.ShoppingMallMapper;
import com.quarkus.model.Hobby;
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

    public Uni<Void> addHobbyToShoppingMall(Integer mallId, Hobby hobby, String author) {
        return mallHobbyProtoService.addHobby(mallMapper.toShoppingMallHobbyCreateRequest(mallId, hobby, author))
            .replaceWithVoid();
    }

    public Uni<Void> deleteHobbyFromShoppingMall(Integer mallId, String author) {
        return mallHobbyProtoService.deleteHobby(mallMapper.toShoppingMallHobbyDeleteRequest(mallId, author))
            .replaceWithVoid();
    }
}
