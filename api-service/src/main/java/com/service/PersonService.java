package com.service;

import com.google.protobuf.BoolValue;
import com.mapper.PersonMapper;
import com.quarkus.model.Person;
import com.quarkus.model.PersonCreateRequest;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import person.PersonProtoService;

@ApplicationScoped
@RequiredArgsConstructor
public class PersonService {

    private final PersonMapper personMapper;

    @GrpcClient("person-service")
    PersonProtoService personGrpcService;

    @CacheResult(cacheName = "personCache")
    public Uni<Person> getPerson(String id, Boolean includeDeleted) {
        return personGrpcService.getPerson(personMapper.toPersonGetRequest(id, includeDeleted))
            .map(personMapper::toPerson);
    }

    @CacheResult(cacheName = "personCache")
    public Uni<Person> getPersonWithHobby(String id, Boolean includeDeleted) {
        return personGrpcService.getPersonWithHobby(personMapper.toPersonGetRequest(id, includeDeleted))
            .map(personMapper::toPerson);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getAllPersons(Boolean includeDeleted) {
        return personGrpcService.getAllPersons(BoolValue.of(includeDeleted)).map(personMapper::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getAllPersonsWithHobbies(Boolean includeDeleted) {
        return personGrpcService.getAllPersonsWithHobbies(BoolValue.of(includeDeleted)).map(personMapper::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getPersonsByHobby(@CacheKey String hobbyName, Boolean includeDeleted) {
        return personGrpcService.getPersonsByHobby(personMapper.toPersonByHobbyRequest(hobbyName, includeDeleted))
            .map(personMapper::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getPersonsWithHobbiesByHobby(@CacheKey String hobbyName, Boolean includeDeleted) {
        return personGrpcService.getPersonsWithHobbiesByHobby(personMapper.toPersonByHobbyRequest(hobbyName,
                includeDeleted))
            .map(personMapper::toPersonList);
    }

    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Person> createPerson(PersonCreateRequest person, String author) {
        return personGrpcService.createPerson(personMapper.toPersonObject(person, author)).map(personMapper::toPerson);
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> updatePerson(@CacheKey String id, Person person, String author) {
        return personGrpcService.updatePerson(personMapper.toPersonObject(id, person, author)).replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> deletePerson(@CacheKey String id, String author) {
        return personGrpcService.deletePerson(personMapper.toDeletePersonRequest(id, author)).replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> restorePerson(@CacheKey String id, String author) {
        return personGrpcService.restorePerson(personMapper.toRestorePersonRequest(id, author)).replaceWithVoid();
    }
}
