package com.service;

import com.entity.Person;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.mapper.PersonMapper;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import person.PersonList;
import person.PersonObject;
import person.PersonProtoService;

@GrpcService
@RequiredArgsConstructor
public class PersonService implements PersonProtoService {

    private final PersonMapper personMapper;

    @Override
    @WithSession
    public Uni<PersonObject> getPerson(StringValue request) {
        return Person.<Person>findById(UUID.fromString(request.getValue()))
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getValue()))
            .invoke(person -> person.setHobbies(new ArrayList<>()))
            .map(personMapper::toPersonObject);
    }

    @Override
    @WithSession
    public Uni<PersonObject> getPersonWithHobby(StringValue request) {
        return Person.findByIdFetchHobbies(UUID.fromString(request.getValue()))
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getValue()))
            .map(personMapper::toPersonObject);
    }

    @Override
    @WithSession
    public Uni<PersonList> getAllPersons(Empty request) {
        return Person.<Person>listAll()
            .onItem()
            .invoke(list -> list.forEach(person -> person.setHobbies(new ArrayList<>())))
            .map(personMapper::toPersonList);
    }

    @Override
    @WithSession
    public Uni<PersonList> getAllPersonsWithHobbies(Empty request) {
        return Person.findAllFetchHobbies().map(personMapper::toPersonList);
    }

    @Override
    @WithSession
    public Uni<PersonList> getPersonsByHobby(StringValue request) {
        return Person.findByHobby(request.getValue())
            .onItem()
            .invoke(list -> list.forEach(person -> person.setHobbies(new ArrayList<>())))
            .map(personMapper::toPersonList);
    }

    @Override
    @WithSession
    public Uni<PersonList> getPersonsWithHobbiesByHobby(StringValue request) {
        return Person.findByHobbyFetchHobbies(request.getValue()).map(personMapper::toPersonList);
    }

    @Override
    @WithSession
    public Uni<PersonObject> createPerson(PersonObject request) {
        return Panache.withTransaction(personMapper.toPerson(request)::<Person>persist)
            .map(personMapper::toPersonObject);
    }

    @Override
    @WithSession
    public Uni<Empty> updatePerson(PersonObject request) {
        return Panache.withTransaction(() -> Person.<Person>findById(UUID.fromString(request.getId()))
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
                .invoke(person -> personMapper.updatePerson(person, request)))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    @WithSession
    public Uni<Empty> deletePerson(StringValue request) {
        return Panache.withTransaction(() -> Person.deleteById(UUID.fromString(request.getValue())))
            .replaceWith(Empty.getDefaultInstance());
    }
}
