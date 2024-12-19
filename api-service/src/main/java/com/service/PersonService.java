package com.service;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.mapper.PersonMapper;
import com.person.model.Person;
import com.person.model.PersonCreateRequest;
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
    public Uni<Person> getPerson(String id) {
        return personGrpcService.getPerson(StringValue.of(id)).map(personMapper::toPerson);
    }

    @CacheResult(cacheName = "personCache")
    public Uni<Person> getPersonWithHobby(String id) {
        return personGrpcService.getPersonWithHobby(StringValue.of(id)).map(personMapper::toPerson);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getAllPersons() {
        return personGrpcService.getAllPersons(Empty.getDefaultInstance()).map(personMapper::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getAllPersonsWithHobbies() {
        return personGrpcService.getAllPersonsWithHobbies(Empty.getDefaultInstance()).map(personMapper::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getPersonsByHobby(@CacheKey String hobbyName) {
        return personGrpcService.getPersonsByHobby(StringValue.of(hobbyName)).map(personMapper::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getPersonsWithHobbiesByHobby(@CacheKey String hobbyName) {
        return personGrpcService.getPersonsWithHobbiesByHobby(StringValue.of(hobbyName))
            .map(personMapper::toPersonList);
    }

    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Person> createPerson(PersonCreateRequest person) {
        return personGrpcService.createPerson(personMapper.toPersonObject(person)).map(personMapper::toPerson);
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> updatePerson(@CacheKey String id, Person person) {
        person.setId(id);
        return personGrpcService.updatePerson(personMapper.toPersonObject(person)).replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> deletePerson(String id) {
        return personGrpcService.deletePerson(StringValue.of(id)).replaceWithVoid();
    }
}
