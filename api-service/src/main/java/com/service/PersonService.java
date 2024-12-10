package com.service;

import static com.service.redis.PersonListRedisService.PERSON_LIST_KEY;

import com.mapper.PersonMapper;
import com.model.Person;
import com.service.redis.PersonListRedisService;
import com.util.PersonUtil;
import grpc.Empty;
import grpc.PersonId;
import grpc.PersonProtoService;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PersonService {

    @GrpcClient("person-service")
    PersonProtoService personGrpcService;

    @Inject
    PersonListRedisService personListRedisService;

    private final PersonMapper mapper = PersonMapper.INSTANCE;

    @CacheResult(cacheName = "personCache")
    public Uni<Person> getPerson(String id) {
        return personGrpcService.getPerson(PersonId.newBuilder().setId(id).build())
            .onItem()
            .transform(PersonUtil::getPerson);
    }

    public Uni<List<Person>> getAllPersons() {
        return personListRedisService.retrieve(PERSON_LIST_KEY)
            .onItem()
            .ifNull()
            .switchTo(() -> personGrpcService.getAllPersons(Empty.getDefaultInstance())
                .onItem()
                .transform(personList -> personList.getPersonList().stream()
                    .map(PersonUtil::getPerson)
                    .toList())
                .onItem()
                .invoke(list -> personListRedisService.save(PERSON_LIST_KEY, list)));
    }

    public Uni<Person> createPerson(Person person) {
        personListRedisService.invalidateCache(PERSON_LIST_KEY);
        return personGrpcService.createPerson(PersonUtil.getPersonObject(person))
            .onItem()
            .transform(PersonUtil::getPerson);
    }

    @CacheInvalidate(cacheName = "personCache")
    public Uni<Empty> updatePerson(@CacheKey String id, Person person) {
        personListRedisService.invalidateCache(PERSON_LIST_KEY);
        Person updatePerson = new Person(id, person.firstName(), person.lastName(), person.age(),
            person.registrationDate());
        return personGrpcService.updatePerson(PersonUtil.getPersonObject(updatePerson));
    }

    @CacheInvalidate(cacheName = "personCache")
    public Uni<Empty> deletePerson(String id) {
        personListRedisService.invalidateCache(PERSON_LIST_KEY);
        return personGrpcService.deletePerson(PersonId.newBuilder().setId(id).build());
    }
}
