package com.service;

import com.entity.Person;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.mapper.PersonMapper;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import person.DeletePersonRequest;
import person.PersonByHobbyRequest;
import person.PersonGetRequest;
import person.PersonList;
import person.PersonObject;
import person.PersonProtoService;
import person.RestorePersonRequest;

@GrpcService
@RequiredArgsConstructor
@WithSession
public class PersonService implements PersonProtoService {

    private final PersonMapper personMapper;

    @Override
    public Uni<PersonObject> getPerson(PersonGetRequest request) {
        Uni<Person> personUni = request.getIncludeDeleted() ? Person.findById(UUID.fromString(request.getId())) :
            Person.findByIdNotDeleted(UUID.fromString(request.getId()));
        return personUni.onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
            .invoke(person -> person.setHobbies(new ArrayList<>()))
            .map(personMapper::toPersonObject);
    }

    @Override
    public Uni<PersonObject> getPersonWithHobby(PersonGetRequest request) {
        Uni<Person> personUni = request.getIncludeDeleted() ?
            Person.findByIdFetchHobbies(UUID.fromString(request.getId())) :
            Person.findByIdFetchHobbiesNotDeleted(UUID.fromString(request.getId()));
        return personUni.onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
            .map(personMapper::toPersonObject);
    }

    @Override
    public Uni<PersonList> getAllPersons(BoolValue includeDeleted) {
        Uni<List<Person>> listUni = includeDeleted.getValue() ? Person.listAll() : Person.findAllNotDeleted();
        return listUni.onItem()
            .invoke(list -> list.forEach(person -> person.setHobbies(new ArrayList<>())))
            .map(personMapper::toPersonList);
    }

    @Override
    public Uni<PersonList> getAllPersonsWithHobbies(BoolValue includeDeleted) {
        Uni<List<Person>> listUni = includeDeleted.getValue() ? Person.findAllFetchHobbies() :
            Person.findAllFetchHobbiesNotDeleted();
        return listUni.map(personMapper::toPersonList);
    }

    @Override
    public Uni<PersonList> getPersonsByHobby(PersonByHobbyRequest request) {
        Uni<List<Person>> listUni = request.getIncludeDeleted() ? Person.findByHobby(request.getHobbyName()) :
            Person.findByHobbyNotDeleted(request.getHobbyName());
        return listUni.onItem()
            .invoke(list -> list.forEach(person -> person.setHobbies(new ArrayList<>())))
            .map(personMapper::toPersonList);
    }

    @Override
    public Uni<PersonList> getPersonsWithHobbiesByHobby(PersonByHobbyRequest request) {
        Uni<List<Person>> listUni = request.getIncludeDeleted() ?
            Person.findByHobbyFetchHobbies(request.getHobbyName()) :
            Person.findByHobbyFetchHobbiesNotDeleted(request.getHobbyName());
        return listUni.map(personMapper::toPersonList);
    }

    @Override
    public Uni<PersonObject> createPerson(PersonObject request) {
        return Panache.withTransaction(personMapper.toPerson(request)::<Person>persist)
            .map(personMapper::toPersonObject);
    }

    @Override
    public Uni<Empty> updatePerson(PersonObject request) {
        return Panache.withTransaction(() -> Person.findByIdNotDeleted(UUID.fromString(request.getId()))
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
                .invoke(person -> personMapper.updatePerson(person, request)))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> deletePerson(DeletePersonRequest request) {
        return Panache.withTransaction(() -> Person.findByIdNotDeleted(UUID.fromString(request.getId()))
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
                .invoke(person -> person.setToDelete(request.getAuthor())))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> restorePerson(RestorePersonRequest request) {
        return Panache.withTransaction(() -> Person.<Person>findById(UUID.fromString(request.getId()))
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
                .invoke(person -> person.setToRestore(request.getAuthor())))
            .replaceWith(Empty.getDefaultInstance());
    }
}
