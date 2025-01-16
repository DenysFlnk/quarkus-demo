package com.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
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
    public Uni<Hobby> getHobby(Integer id) {
        return hobbyProtoService.getHobby(Int32Value.of(id)).map(hobbyMapper::toHobby);
    }

    @CacheResult(cacheName = "hobbyListCache")
    public Uni<List<Hobby>> getAllHobbies() {
        return hobbyProtoService.getAllHobbies(Empty.getDefaultInstance()).map(hobbyMapper::toHobbyList);
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
}
