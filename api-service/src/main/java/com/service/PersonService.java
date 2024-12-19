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
import person.PersonProtoService;

@ApplicationScoped
public class PersonService {

    private static final PersonMapper PERSON_MAPPER = PersonMapper.INSTANCE;

    @GrpcClient("person-service")
    PersonProtoService personGrpcService;

    @CacheResult(cacheName = "personCache")
    public Uni<Person> getPerson(String id) {
        return personGrpcService.getPerson(StringValue.of(id)).map(PERSON_MAPPER::toPerson);
    }

    @CacheResult(cacheName = "personCache")
    public Uni<Person> getPersonWithHobby(String id) {
        return personGrpcService.getPersonWithHobby(StringValue.of(id)).map(PERSON_MAPPER::toPerson);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getAllPersons() {
        return personGrpcService.getAllPersons(Empty.getDefaultInstance()).map(PERSON_MAPPER::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getAllPersonsWithHobbies() {
        return personGrpcService.getAllPersonsWithHobbies(Empty.getDefaultInstance()).map(PERSON_MAPPER::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getPersonsByHobby(@CacheKey String hobbyName) {
        return personGrpcService.getPersonsByHobby(StringValue.of(hobbyName)).map(PERSON_MAPPER::toPersonList);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getPersonsWithHobbiesByHobby(@CacheKey String hobbyName) {
        return personGrpcService.getPersonsWithHobbiesByHobby(StringValue.of(hobbyName))
            .map(PERSON_MAPPER::toPersonList);
    }

    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Person> createPerson(PersonCreateRequest person) {
        return personGrpcService.createPerson(PERSON_MAPPER.toPersonObject(person)).map(PERSON_MAPPER::toPerson);
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> updatePerson(@CacheKey String id, Person person) {
        person.setId(id);
        return personGrpcService.updatePerson(PERSON_MAPPER.toPersonObject(person)).replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> deletePerson(String id) {
        return personGrpcService.deletePerson(StringValue.of(id)).replaceWithVoid();
    }
}
