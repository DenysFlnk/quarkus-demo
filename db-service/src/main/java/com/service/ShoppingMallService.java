package com.service;

import com.entity.ShoppingMall;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.mapper.ShoppingMallMapper;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import java.util.ArrayList;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;
import shopping_mall.ShoppingMallProtoService;

@GrpcService
public class ShoppingMallService implements ShoppingMallProtoService {

    private final ShoppingMallMapper MALL_MAPPER = ShoppingMallMapper.INSTANCE;

    @Override
    @WithSession
    public Uni<ShoppingMallList> getAllMalls(Empty request) {
        return ShoppingMall.<ShoppingMall>listAll()
            .onItem()
            .transform(MALL_MAPPER::toShoppingMallList);
    }

    @Override
    @WithSession
    public Uni<ShoppingMallObject> getMall(Int32Value request) {
        return ShoppingMall.<ShoppingMall>findById(request.getValue())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getValue()))
            .onItem()
            .ifNotNull()
            .transform(MALL_MAPPER::toShoppingMallObject);
    }

    @Override
    @WithSession
    public Uni<ShoppingMallObject> createMall(ShoppingMallObject request) {
        System.out.println(request.toString());
        ShoppingMall shoppingMall = MALL_MAPPER.toShoppingMall(request);
        System.out.println("Shopping mall id: " + shoppingMall.getId());
        return Panache.withTransaction(shoppingMall::<ShoppingMall>persist)
            .onItem()
            .transform(MALL_MAPPER::toShoppingMallObject);
    }

    @Override
    @WithSession
    public Uni<Empty> updateMall(ShoppingMallObject request) {
        return Panache.withTransaction(() -> ShoppingMall.<ShoppingMall>findById(request.getId())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getId()))
            .onItem()
            .ifNotNull()
            .invoke(mall -> MALL_MAPPER.updateShoppingMall(mall, request)))
            .onItem()
            .transform(mall -> Empty.getDefaultInstance());
    }

    @Override
    @WithSession
    public Uni<Empty> deleteMall(Int32Value request) {
        return Panache.withTransaction(() -> ShoppingMall.deleteById(request.getValue()))
            .onItem()
            .transform(b -> Empty.getDefaultInstance());
    }
}
