package com.controller;

import com.quarkus.api.PersonHobbyControllerApi;
import com.quarkus.model.Hobby;
import com.service.PersonHobbyService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@Authenticated
public class PersonHobbyController implements PersonHobbyControllerApi {

    @Inject
    PersonHobbyService personHobbyService;

    @Inject
    SecurityIdentity securityIdentity;

    @Override
    public Uni<Void> addHobbyToPerson(String personId, Hobby hobby) {
        return personHobbyService.addHobbyToPerson(personId, hobby, securityIdentity.getPrincipal().getName());
    }

    @Override
    public Uni<Void> deletePersonHobby(Integer personHobbyId) {
        return personHobbyService.removeHobbyFromPerson(personHobbyId, securityIdentity.getPrincipal().getName());
    }
}
