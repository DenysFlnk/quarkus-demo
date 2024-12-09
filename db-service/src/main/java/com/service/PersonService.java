package com.service;

import com.entity.Person;
import com.util.PersonUtil;
import grpc.Empty;
import grpc.PersonId;
import grpc.PersonList;
import grpc.PersonObject;
import grpc.PersonProtoService;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import java.time.LocalDate;
import java.util.List;

@GrpcService
public class PersonService implements PersonProtoService {

    @Override
    @WithSession
    public Uni<PersonObject> getPerson(PersonId request) {
        return Person.<Person>findById(request.getId())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
            .onItem()
            .ifNotNull()
            .transform(PersonUtil::getProtoPersonObject);
    }

    @Override
    @WithSession
    public Uni<PersonList> getAllPersons(Empty request) {
        return Person.<Person>listAll()
            .onItem()
            .transform(persons -> {
                List<PersonObject> list = persons.stream()
                    .map(PersonUtil::getProtoPersonObject)
                    .toList();
                return PersonList.newBuilder().addAllPerson(list).build();
            });
    }

    @Override
    @WithSession
    public Uni<PersonObject> createPerson(PersonObject request) {
        return Panache.withTransaction(PersonUtil.getPerson(request)::<Person>persist)
            .onItem()
            .transform(PersonUtil::getProtoPersonObject);
    }

    @Override
    @WithSession
    public Uni<Empty> updatePerson(PersonObject request) {
        return Panache.withTransaction(() -> Person.<Person>findById(request.getId())
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
                .onItem()
                .ifNotNull()
                .invoke(person -> PersonUtil.updatePerson(person, request)))
            .onItem()
            .transform(person -> Empty.getDefaultInstance());
    }

    @Override
    @WithSession
    public Uni<Empty> deletePerson(PersonId request) {
        return Panache.withTransaction(() -> Person.deleteById(request.getId()))
            .onItem()
            .transform(b -> Empty.getDefaultInstance());
    }
}
