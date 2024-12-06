package com.service;

import com.entity.Person;
import grpc.PersonProtoService;
import grpc.PersonRequest;
import grpc.PersonResponse;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;

@GrpcService
public class PersonService implements PersonProtoService {

    @Override
    @WithSession
    public Uni<PersonResponse> getPerson(PersonRequest request) {
        return Person.<Person>findById(request.getId())
            .onItem()
            .ifNull()
            .failWith(() -> new IllegalArgumentException("Invalid person id: " + request.getId()))
            .onItem()
            .ifNotNull()
            .transform(person ->
                PersonResponse.newBuilder().setId(person.getId())
                    .setFirstName(person.getFirstName())
                    .setLastName(person.getLastName())
                    .setAge(person.getAge())
                    .setRegistrationDateInEpochDays(person.getRegistrationDate().toEpochDay())
                    .build());
    }
}
