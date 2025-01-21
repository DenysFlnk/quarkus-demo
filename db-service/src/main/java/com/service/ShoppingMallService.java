package com.service;

import com.entity.ShoppingMall;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.mapper.ShoppingMallMapper;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shopping_mall.DeleteMallRequest;
import shopping_mall.RestoreMallRequest;
import shopping_mall.GetMallRequest;
import shopping_mall.RestrictedMallIds;
import shopping_mall.ShoppingMallList;
import shopping_mall.ShoppingMallObject;
import shopping_mall.ShoppingMallProtoService;

@GrpcService
@RequiredArgsConstructor
@WithSession
public class ShoppingMallService implements ShoppingMallProtoService {

    private final ShoppingMallMapper mallMapper;

    @Override
    public Uni<ShoppingMallList> getAllMalls(BoolValue includeDeleted) {
        Uni<List<ShoppingMall>> listUni = includeDeleted.getValue() ? ShoppingMall.findAllFetchHobbies() :
            ShoppingMall.findAllNotDeletedFetchHobbies();
        return listUni.map(mallMapper::toShoppingMallList);
    }

    @Override
    public Uni<ShoppingMallList> getMallsWithoutRestricted(RestrictedMallIds request) {
        List<Integer> restrictedMalls = mallMapper.toIntegerList(request);
        Uni<List<ShoppingMall>> listUni = request.getIncludeDeleted() ?
            ShoppingMall.findAllFetchHobbiesExcept(restrictedMalls) :
            ShoppingMall.findAllNotDeletedFetchHobbiesExcept(restrictedMalls);
        return listUni.map(mallMapper::toShoppingMallList);
    }

    @Override
    public Uni<ShoppingMallObject> getMall(GetMallRequest request) {
        Uni<ShoppingMall> mallUni = request.getIncludeDeleted() ? ShoppingMall.findByIdFetchHobby(request.getId()) :
            ShoppingMall.findByIdNotDeletedFetchHobby(request.getId());
        return mallUni.onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getId()))
            .map(mallMapper::toShoppingMallObject);
    }

    @Override
    public Uni<ShoppingMallObject> createMall(ShoppingMallObject request) {
        return Panache.withTransaction(mallMapper.toShoppingMall(request)::<ShoppingMall>persist)
            .map(mallMapper::toShoppingMallObject);
    }

    @Override
    public Uni<Empty> updateMall(ShoppingMallObject request) {
        return Panache.withTransaction(() -> ShoppingMall.findByIdNotDeleted(request.getId())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getId()))
            .invoke(mall -> mallMapper.updateShoppingMall(mall, request)))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> deleteMall(DeleteMallRequest request) {
        return Panache.withTransaction(() ->
                ShoppingMall.findByIdNotDeleted(request.getId())
                    .onItem()
                    .ifNull()
                    .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getId()))
                    .invoke(mall -> mall.setToDelete(request.getAuthor())))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> restoreMall(RestoreMallRequest request) {
        return Panache.withTransaction(() ->
                ShoppingMall.<ShoppingMall>findById(request.getId())
                    .onItem()
                    .ifNull()
                    .failWith(() -> new IllegalArgumentException("Invalid mall id: " + request.getId()))
                    .invoke(mall -> mall.setToRestore(request.getAuthor())))
            .replaceWith(Empty.getDefaultInstance());
    }
}
