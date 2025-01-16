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
import person.DeletePersonRequest;
import person.PersonList;
import person.PersonObject;
import person.PersonProtoService;

@GrpcService
@RequiredArgsConstructor
@WithSession
public class PersonService implements PersonProtoService {

    private final PersonMapper personMapper;

    @Override
    public Uni<PersonObject> getPerson(StringValue request) {
        return Person.<Person>findById(UUID.fromString(request.getValue()))
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getValue()))
            .invoke(person -> person.setHobbies(new ArrayList<>()))
            .map(personMapper::toPersonObject);
    }

    @Override
    public Uni<PersonObject> getPersonWithHobby(StringValue request) {
        return Person.findByIdFetchHobbies(UUID.fromString(request.getValue()))
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getValue()))
            .map(personMapper::toPersonObject);
    }

    @Override
    public Uni<PersonList> getAllPersons(Empty request) {
        return Person.<Person>listAll()
            .onItem()
            .invoke(list -> list.forEach(person -> person.setHobbies(new ArrayList<>())))
            .map(personMapper::toPersonList);
    }

    @Override
    public Uni<PersonList> getAllPersonsWithHobbies(Empty request) {
        return Person.findAllFetchHobbies().map(personMapper::toPersonList);
    }

    @Override
    public Uni<PersonList> getPersonsByHobby(StringValue request) {
        return Person.findByHobby(request.getValue())
            .onItem()
            .invoke(list -> list.forEach(person -> person.setHobbies(new ArrayList<>())))
            .map(personMapper::toPersonList);
    }

    @Override
    public Uni<PersonList> getPersonsWithHobbiesByHobby(StringValue request) {
        return Person.findByHobbyFetchHobbies(request.getValue()).map(personMapper::toPersonList);
    }

    @Override
    public Uni<PersonObject> createPerson(PersonObject request) {
        return Panache.withTransaction(personMapper.toPerson(request)::<Person>persist)
            .map(personMapper::toPersonObject);
    }

    @Override
    public Uni<Empty> updatePerson(PersonObject request) {
        return Panache.withTransaction(() -> Person.<Person>findById(UUID.fromString(request.getId()))
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
                .invoke(person -> personMapper.updatePerson(person, request)))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> deletePerson(DeletePersonRequest request) {
        return Panache.withTransaction(() -> Person.<Person>findById(UUID.fromString(request.getId()))
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
                .flatMap(person -> {
                    person.setAuthor(request.getAuthor());
                    return person.persistAndFlush()
                        .flatMap(ignored -> Person.deleteById(UUID.fromString(person.getId())));
                }))
            .replaceWith(Empty.getDefaultInstance());
    }
}
