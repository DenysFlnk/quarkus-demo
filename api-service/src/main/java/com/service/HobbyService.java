package com.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.mapper.HobbyMapper;
import com.person.model.Hobby;
import com.person.model.HobbyCreateRequest;
import hobby.HobbyProtoService;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class HobbyService {

    private static final HobbyMapper HOBBY_MAPPER = HobbyMapper.INSTANCE;

    @GrpcClient("hobby-service")
    HobbyProtoService hobbyProtoService;

    @CacheResult(cacheName = "hobbyCache")
    public Uni<Hobby> getHobby(Integer id) {
        return hobbyProtoService.getHobby(Int32Value.of(id))
            .onItem()
            .transform(HOBBY_MAPPER::toHobby);
    }

    @CacheResult(cacheName = "hobbyListCache")
    public Uni<List<Hobby>> getAllHobbies() {
        return hobbyProtoService.getAllHobbies(Empty.getDefaultInstance())
            .onItem()
            .transform(HOBBY_MAPPER::toHobbyList);
    }

    @CacheInvalidate(cacheName = "hobbyCache")
    @CacheInvalidateAll(cacheName = "hobbyListCache")
    public Uni<Void> updateHobby(@CacheKey Integer id, Hobby hobby) {
        hobby.setId(id);
        return hobbyProtoService.updateHobby(HOBBY_MAPPER.toHobbyObject(hobby))
            .onItem()
            .transform(item -> Uni.createFrom().voidItem())
            .replaceWithVoid();
    }

    @CacheInvalidateAll(cacheName = "hobbyListCache")
    public Uni<Hobby> createHobby(HobbyCreateRequest createRequest) {
        return hobbyProtoService.createHobby(StringValue.of(createRequest.getName()))
            .onItem()
            .transform(HOBBY_MAPPER::toHobby);
    }

    @CacheInvalidate(cacheName = "hobbyCache")
    @CacheInvalidateAll(cacheName = "hobbyListCache")
    public Uni<Void> deleteHobby(@CacheKey Integer id) {
        return hobbyProtoService.deleteHobby(Int32Value.of(id))
            .onItem()
            .transform(item -> Uni.createFrom().voidItem())
            .replaceWithVoid();
    }
}
