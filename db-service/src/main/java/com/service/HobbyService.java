package com.service;

import com.entity.Hobby;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.mapper.HobbyMapper;
import hobby.HobbyList;
import hobby.HobbyObject;
import hobby.HobbyProtoService;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;

@GrpcService
public class HobbyService implements HobbyProtoService {

    private static final HobbyMapper HOBBY_MAPPER = HobbyMapper.INSTANCE;

    @Override
    @WithSession
    public Uni<HobbyObject> getHobby(Int32Value request) {
        return Hobby.<Hobby>findById(request.getValue())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getValue()))
            .onItem()
            .ifNotNull()
            .transform(HOBBY_MAPPER::toHobbyObject);
    }

    @Override
    @WithSession
    public Uni<HobbyList> getAllHobbies(Empty request) {
        return Hobby.<Hobby>listAll()
            .onItem()
            .transform(HOBBY_MAPPER::toHobbyList);
    }

    @Override
    @WithSession
    public Uni<HobbyObject> createHobby(StringValue request) {
        return Panache.withTransaction(HOBBY_MAPPER.toHobby(request)::<Hobby>persist)
            .onItem()
            .transform(HOBBY_MAPPER::toHobbyObject);
    }

    @Override
    @WithSession
    public Uni<Empty> updateHobby(HobbyObject request) {
        return Panache.withTransaction(() -> Hobby.<Hobby>findById(request.getId())
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid hobby id: " + request.getId()))
                .onItem()
                .ifNotNull()
                .invoke(hobby -> HOBBY_MAPPER.updateHobby(hobby, request)))
            .onItem()
            .transform(item -> Empty.getDefaultInstance());
    }

    @Override
    @WithSession
    public Uni<Empty> deleteHobby(Int32Value request) {
        return Panache.withTransaction(() -> Hobby.deleteById(request.getValue()))
            .onItem()
            .transform(b -> Empty.getDefaultInstance());
    }
}
