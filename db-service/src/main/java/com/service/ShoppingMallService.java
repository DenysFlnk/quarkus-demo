package com.service;

import com.entity.ShoppingMall;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.mapper.ShoppingMallMapper;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;
import shopping_mall.ShoppingMallProtoService;
import shopping_mall.UpdateStatusRequest;

@GrpcService
public class ShoppingMallService implements ShoppingMallProtoService {

    private static final ShoppingMallMapper MALL_MAPPER = ShoppingMallMapper.INSTANCE;

    @Override
    @WithSession
    public Uni<ShoppingMallList> getAllMalls(Empty request) {
        return ShoppingMall.<ShoppingMall>listAll().map(MALL_MAPPER::toShoppingMallList);
    }

    @Override
    @WithSession
    public Uni<ShoppingMallObject> getMall(Int32Value request) {
        return ShoppingMall.<ShoppingMall>findById(request.getValue())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getValue()))
            .map(MALL_MAPPER::toShoppingMallObject);
    }

    @Override
    @WithSession
    public Uni<ShoppingMallObject> createMall(ShoppingMallObject request) {
        return Panache.withTransaction(MALL_MAPPER.toShoppingMall(request)::<ShoppingMall>persist)
            .map(MALL_MAPPER::toShoppingMallObject);
    }

    @Override
    @WithSession
    public Uni<Empty> updateMall(ShoppingMallObject request) {
        return Panache.withTransaction(() -> ShoppingMall.<ShoppingMall>findById(request.getId())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getId()))
            .invoke(mall -> MALL_MAPPER.updateShoppingMall(mall, request)))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> updateMallStatus(UpdateStatusRequest request) {
        return Panache.withTransaction(() -> ShoppingMall.<ShoppingMall>findById(request.getMallId())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getMallId()))
            .invoke(mall -> mall.setOperationalStatus(MALL_MAPPER.toOperationalStatus(request))))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    @WithSession
    public Uni<Empty> deleteMall(Int32Value request) {
        return Panache.withTransaction(() -> ShoppingMall.deleteById(request.getValue()))
            .replaceWith(Empty.getDefaultInstance());
    }
}
