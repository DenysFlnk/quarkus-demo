package com.service;

import com.entity.Hobby;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.mapper.HobbyMapper;
import hobby.HobbyCreateRequest;
import hobby.HobbyDeleteRequest;
import hobby.HobbyList;
import hobby.HobbyObject;
import hobby.HobbyProtoService;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
@WithSession
public class HobbyService implements HobbyProtoService {

    private final HobbyMapper hobbyMapper;

    @Override
    public Uni<HobbyObject> getHobby(Int32Value request) {
        return Hobby.<Hobby>findById(request.getValue())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getValue()))
            .map(hobbyMapper::toHobbyObject);
    }

    @Override
    public Uni<HobbyList> getAllHobbies(Empty request) {
        return Hobby.<Hobby>listAll().map(hobbyMapper::toHobbyList);
    }

    @Override
    public Uni<HobbyObject> createHobby(HobbyCreateRequest request) {
        return Panache.withTransaction(hobbyMapper.toHobby(request)::<Hobby>persist).map(hobbyMapper::toHobbyObject);
    }

    @Override
    public Uni<Empty> updateHobby(HobbyObject request) {
        return Panache.withTransaction(() -> Hobby.<Hobby>findById(request.getId())
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getId()))
                .invoke(hobby -> hobbyMapper.updateHobby(hobby, request)))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> deleteHobby(HobbyDeleteRequest request) {
        return Panache.withTransaction(() -> Hobby.<Hobby>findById(request.getId())
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getId()))
                .flatMap(hobby -> {
                    hobby.setAuthor(request.getAuthor());
                    return hobby.persistAndFlush()
                        .flatMap(ignored -> Hobby.deleteById(hobby.getId()));
                }))
            .replaceWith(Empty.getDefaultInstance());
    }
}
