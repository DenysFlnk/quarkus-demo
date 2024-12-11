package com.service;

import com.person.model.Person;
import com.util.PersonUtil;
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

    @GrpcClient("person-service")
    PersonProtoService personGrpcService;

    @CacheResult(cacheName = "personCache")
    public Uni<Person> getPerson(String id) {
        return personGrpcService.getPerson(PersonId.newBuilder().setId(id).build())
            .onItem()
            .transform(PersonUtil::getPerson);
    }

    @CacheResult(cacheName = "personListCache")
    public Uni<List<Person>> getAllPersons() {
        System.out.println("Enter getAllPersons");
        return personGrpcService.getAllPersons(Empty.getDefaultInstance())
            .onItem()
            .transform(personList -> personList.getPersonList().stream()
                .map(PersonUtil::getPerson)
                .toList());
    }

    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Person> createPerson(Person person) {
        return personGrpcService.createPerson(PersonUtil.getPersonObject(person))
            .onItem()
            .transform(PersonUtil::getPerson);
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> updatePerson(@CacheKey String id, Person person) {
        person.setId(id);
        return personGrpcService.updatePerson(PersonUtil.getPersonObject(person))
            .onItem()
            .transform(item -> Uni.createFrom().voidItem())
            .replaceWithVoid();
    }

    @CacheInvalidate(cacheName = "personCache")
    @CacheInvalidateAll(cacheName = "personListCache")
    public Uni<Void> deletePerson(String id) {
        return personGrpcService.deletePerson(PersonId.newBuilder().setId(id).build())
            .onItem()
            .transform(item -> Uni.createFrom().voidItem())
            .replaceWithVoid();
    }
}
