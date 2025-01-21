package com.service;

import com.entity.Hobby;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.mapper.HobbyMapper;
import hobby.HobbyCreateRequest;
import hobby.HobbyDeleteRequest;
import hobby.HobbyGetRequest;
import hobby.HobbyList;
import hobby.HobbyObject;
import hobby.HobbyProtoService;
import hobby.HobbyRestoreRequest;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import java.util.List;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
@WithSession
public class HobbyService implements HobbyProtoService {

    private final HobbyMapper hobbyMapper;

    @Override
    public Uni<HobbyObject> getHobby(HobbyGetRequest request) {
        Uni<Hobby> hobbyUni = request.getIncludeDeleted() ? Hobby.findById(request.getId()) :
            Hobby.findByIdNotDeleted(request.getId());
        return hobbyUni.onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getId()))
            .map(hobbyMapper::toHobbyObject);
    }

    @Override
    public Uni<HobbyList> getAllHobbies(BoolValue includeDeleted) {
        Uni<List<Hobby>> listUni = includeDeleted.getValue() ? Hobby.listAll() : Hobby.findAllNotDeleted();
        return listUni.map(hobbyMapper::toHobbyList);
    }

    @Override
    public Uni<HobbyObject> createHobby(HobbyCreateRequest request) {
        return Panache.withTransaction(hobbyMapper.toHobby(request)::<Hobby>persist).map(hobbyMapper::toHobbyObject);
    }

    @Override
    public Uni<Empty> updateHobby(HobbyObject request) {
        return Panache.withTransaction(() -> Hobby.findByIdNotDeleted(request.getId())
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getId()))
                .invoke(hobby -> hobbyMapper.updateHobby(hobby, request)))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> deleteHobby(HobbyDeleteRequest request) {
        return Panache.withTransaction(() -> Hobby.findByIdNotDeleted(request.getId())
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getId()))
                .invoke(hobby -> hobby.setToDelete(request.getAuthor())))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> restoreHobby(HobbyRestoreRequest request) {
        return Panache.withTransaction(() -> Hobby.<Hobby>findById(request.getId())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getId()))
            .invoke(hobby -> hobby.setToRestore(request.getAuthor())))
            .replaceWith(Empty.getDefaultInstance());
    }
}
