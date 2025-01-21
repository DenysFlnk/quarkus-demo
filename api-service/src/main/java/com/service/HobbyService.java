package com.service;

import com.google.protobuf.BoolValue;
import com.mapper.HobbyMapper;
import com.quarkus.model.Hobby;
import com.quarkus.model.HobbyCreateRequest;
import hobby.HobbyProtoService;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyMapper hobbyMapper;

    @GrpcClient("hobby-service")
    HobbyProtoService hobbyProtoService;

    @CacheResult(cacheName = "hobbyCache")
    public Uni<Hobby> getHobby(Integer id, Boolean includeDeleted) {
        return hobbyProtoService.getHobby(hobbyMapper.toHobbyGetRequest(id, includeDeleted)).map(hobbyMapper::toHobby);
    }

    @CacheResult(cacheName = "hobbyListCache")
    public Uni<List<Hobby>> getAllHobbies(Boolean includeDeleted) {
        return hobbyProtoService.getAllHobbies(BoolValue.of(includeDeleted)).map(hobbyMapper::toHobbyList);
    }

    @CacheInvalidate(cacheName = "hobbyCache")
    @CacheInvalidateAll(cacheName = "hobbyListCache")
    public Uni<Void> updateHobby(@CacheKey Integer id, Hobby hobby, String author) {
        hobby.setId(id);
        return hobbyProtoService.updateHobby(hobbyMapper.toHobbyObject(id, hobby, author)).replaceWithVoid();
    }

    @CacheInvalidateAll(cacheName = "hobbyListCache")
    public Uni<Hobby> createHobby(HobbyCreateRequest createRequest, String author) {
        return hobbyProtoService.createHobby(hobbyMapper.toHobbyCreateRequest(createRequest, author))
            .map(hobbyMapper::toHobby);
    }

    @CacheInvalidate(cacheName = "hobbyCache")
    @CacheInvalidateAll(cacheName = "hobbyListCache")
    public Uni<Void> deleteHobby(@CacheKey Integer id, String author) {
        return hobbyProtoService.deleteHobby(hobbyMapper.toHobbyDeleteRequest(id, author)).replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "hobbyCache")
    @CacheInvalidateAll(cacheName = "hobbyListCache")
    public Uni<Void> restoreHobby(@CacheKey Integer id, String author) {
        return hobbyProtoService.restoreHobby(hobbyMapper.toHobbyRestoreRequest(id, author)).replaceWithVoid();
    }
}
