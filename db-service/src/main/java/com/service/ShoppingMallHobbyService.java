package com.service;

import com.entity.ShoppingMall;
import com.entity.ShoppingMallHobby;
import com.google.protobuf.Empty;
import com.mapper.ShoppingMallMapper;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import shopping_mall_hobby.ShoppingMallHobbyCreateRequest;
import shopping_mall_hobby.ShoppingMallHobbyDeleteRequest;
import shopping_mall_hobby.ShoppingMallHobbyProtoService;

@GrpcService
@RequiredArgsConstructor
@WithSession
public class ShoppingMallHobbyService implements ShoppingMallHobbyProtoService {

    private final ShoppingMallMapper mallMapper;

    @Override
    public Uni<Empty> addHobby(ShoppingMallHobbyCreateRequest request) {
        return Panache.withTransaction(() -> ShoppingMall.<ShoppingMall>findById(request.getMallId())
                .flatMap(mall -> {
                    ShoppingMallHobby shoppingMallHobby = mallMapper.toShoppingMallHobby(request);
                    shoppingMallHobby.setShoppingMall(mall);
                    return shoppingMallHobby.persist();
                }))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> deleteHobby(ShoppingMallHobbyDeleteRequest request) {
        return Panache.withTransaction(() ->
                ShoppingMallHobby.<ShoppingMallHobby>findById(request.getShoppingMallHobbyId())
                    .onItem()
                    .ifNull()
                    .failWith(() -> new IllegalArgumentException(
                        "Invalid ShoppingMallHobby id: " + request.getShoppingMallHobbyId()))
                    .flatMap(mallHobby -> {
                        mallHobby.setAuthor(request.getAuthor());
                        return mallHobby.persistAndFlush()
                            .flatMap(ignored -> ShoppingMallHobby.deleteById(mallHobby.getId()));
                    }))
            .replaceWith(Empty.getDefaultInstance());
    }
}
