package com.service;

import com.entity.Person;
import com.entity.PersonHobby;
import com.google.protobuf.Empty;
import com.mapper.PersonMapper;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import person_hobby.PersonHobbyCreateRequest;
import person_hobby.PersonHobbyDeleteRequest;
import person_hobby.PersonHobbyProtoService;
import person_hobby.PersonHobbyRestoreRequest;

@GrpcService
@RequiredArgsConstructor
@WithSession
public class PersonHobbyService implements PersonHobbyProtoService {

    private final PersonMapper personMapper;

    @Override
    public Uni<Empty> addHobby(PersonHobbyCreateRequest request) {
        return Panache.withTransaction(() -> Person.findByIdNotDeleted(UUID.fromString(request.getPersonId()))
                .flatMap(person -> {
                    PersonHobby personHobby = personMapper.toPersonHobby(request);
                    personHobby.setPerson(person);
                    return personHobby.persist();
                }))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> deleteHobby(PersonHobbyDeleteRequest request) {
        return Panache.withTransaction(() -> PersonHobby.findByIdNotDeleted(request.getPersonHobbyId())
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid PersonHobby id: " + request.getPersonHobbyId()))
                .invoke(personHobby -> personHobby.setToDelete(request.getAuthor())))
            .replaceWith(Empty.getDefaultInstance());
    }

    @Override
    public Uni<Empty> restoreHobby(PersonHobbyRestoreRequest request) {
        return Panache.withTransaction(() -> PersonHobby.<PersonHobby>findById(request.getPersonHobbyId())
                .onItem()
                .ifNull()
                .failWith(() -> new IllegalArgumentException("Invalid PersonHobby id: " + request.getPersonHobbyId()))
                .invoke(personHobby -> personHobby.setToRestore(request.getAuthor())))
            .replaceWith(Empty.getDefaultInstance());
    }
}
