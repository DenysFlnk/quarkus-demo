package com.service;

import com.mapper.PersonMapper;
import com.quarkus.model.Hobby;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import person_hobby.PersonHobbyProtoService;

@ApplicationScoped
@RequiredArgsConstructor
public class PersonHobbyService {

    private final PersonMapper personMapper;

    @GrpcClient("person-hobby-service")
    PersonHobbyProtoService personHobbyProtoService;

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> addHobbyToPerson(@CacheKey String personId, Hobby hobby, String author) {
        return personHobbyProtoService.addHobby(personMapper.toPersonHobbyCreateRequest(personId, hobby, author))
            .replaceWithVoid();
    }

    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> removeHobbyFromPerson(Integer personHobbyId, String author) {
        return personHobbyProtoService.deleteHobby(personMapper.toPersonHobbyDeleteRequest(personHobbyId, author))
            .replaceWithVoid();
    }

    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> restoreHobbyInPerson(Integer personHobbyId, String author) {
        return personHobbyProtoService.restoreHobby(personMapper.toPersonHobbyRestoreRequest(personHobbyId, author))
            .replaceWithVoid();
    }
}
