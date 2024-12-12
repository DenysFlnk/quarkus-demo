package com.service;

import com.entity.Person;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.mapper.PersonMapper;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import java.util.UUID;
import person.PersonList;
import person.PersonObject;
import person.PersonProtoService;

@GrpcService
public class PersonService implements PersonProtoService {

    private static final PersonMapper PERSON_MAPPER = PersonMapper.INSTANCE;

    @Override
    @WithSession
    public Uni<PersonObject> getPerson(StringValue request) {
        return Person.<Person>findById(UUID.fromString(request.getValue()))
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getValue()))
            .onItem()
            .ifNotNull()
            .transform(PERSON_MAPPER::toPersonObject);
    }

    @Override
    @WithSession
    public Uni<PersonList> getAllPersons(Empty request) {
        return Person.<Person>listAll()
            .onItem()
            .transform(PERSON_MAPPER::toPersonList);
    }

    @Override
    @WithSession
    public Uni<PersonList> getPersonsByHobby(StringValue request) {
        return Person.getPersonsByHobby(request.getValue())
            .onItem()
            .transform(PERSON_MAPPER::toPersonList);
    }

    @Override
    @WithSession
    public Uni<PersonObject> createPerson(PersonObject request) {
        return Panache.withTransaction(PERSON_MAPPER.toPerson(request)::<Person>persist)
            .onItem()
            .transform(PERSON_MAPPER::toPersonObject);
    }

    @Override
    @WithSession
    public Uni<Empty> updatePerson(PersonObject request) {
        return Panache.withTransaction(() -> Person.<Person>findById(UUID.fromString(request.getId()))
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
                .onItem()
                .ifNotNull()
                .invoke(person -> PERSON_MAPPER.updatePerson(person, request)))
            .onItem()
            .transform(person -> Empty.getDefaultInstance());
    }

    @Override
    @WithSession
    public Uni<Empty> deletePerson(StringValue request) {
        return Panache.withTransaction(() -> Person.deleteById(UUID.fromString(request.getValue())))
            .onItem()
            .transform(b -> Empty.getDefaultInstance());
    }
}
