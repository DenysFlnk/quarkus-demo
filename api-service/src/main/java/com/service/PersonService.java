package com.service;

import com.mapper.PersonMapper;
import com.person.model.Person;
import com.person.model.PersonCreateRequest;
import grpc.Empty;
import grpc.PersonId;
import grpc.PersonProtoService;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;


@ApplicationScoped
public class PersonService {

    private static final PersonMapper MAPPER = PersonMapper.INSTANCE;

    @GrpcClient("person-service")
    PersonProtoService personGrpcService;

    @CacheResult(cacheName = "personCache")
    public Uni<Person> getPerson(String id) {
        return personGrpcService.getPerson(PersonId.newBuilder().setId(id).build())
            .onItem()
            .transform(MAPPER::toPerson);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getAllPersons() {
        return personGrpcService.getAllPersons(Empty.getDefaultInstance())
            .onItem()
            .transform(personList -> personList.getPersonList().stream()
                .map(MAPPER::toPerson)
                .toList());
    }

    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Person> createPerson(PersonCreateRequest person) {
        return personGrpcService.createPerson(MAPPER.toPersonObject(person))
            .onItem()
            .transform(MAPPER::toPerson);
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> updatePerson(@CacheKey String id, Person person) {
        person.setId(id);
        return personGrpcService.updatePerson(MAPPER.toPersonObject(person))
            .onItem()
            .transform(item -> Uni.createFrom().voidItem())
            .replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> deletePerson(String id) {
        return personGrpcService.deletePerson(MAPPER.toPersonId(id))
            .onItem()
            .transform(item -> Uni.createFrom().voidItem())
            .replaceWithVoid();
    }
}
