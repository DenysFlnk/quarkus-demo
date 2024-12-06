package com.service;

import com.entity.Person;
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
            .transform(PersonService::getPersonObject);
    }

    @Override
    @WithSession
    public Uni<PersonList> getAllPersons(Empty request) {
        return Person.<Person>listAll()
            .onItem()
            .transform(persons -> {
                List<PersonObject> list = persons.stream()
                    .map(PersonService::getPersonObject)
                    .toList();
                return PersonList.newBuilder().addAllPerson(list).build();
            });
    }

    @Override
    @WithSession
    public Uni<PersonObject> createPerson(PersonObject request) {
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setAge(request.getAge());
        person.setRegistrationDate(LocalDate.ofEpochDay(request.getRegistrationDateInEpochDays()));

        return Panache.withTransaction(person::<Person>persist)
            .onItem()
            .transform(PersonService::getPersonObject);
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
                .invoke(person -> {
                    person.setFirstName(request.getFirstName());
                    person.setLastName(request.getLastName());
                    person.setAge(request.getAge());
                    person.setRegistrationDate(LocalDate.ofEpochDay(request.getRegistrationDateInEpochDays()));
                }))
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

    private static PersonObject getPersonObject(Person person) {
        return PersonObject.newBuilder().setId(person.getId())
            .setFirstName(person.getFirstName())
            .setLastName(person.getLastName())
            .setAge(person.getAge())
            .setRegistrationDateInEpochDays(person.getRegistrationDate().toEpochDay())
            .build();
    }
}
