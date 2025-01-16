package com.controller;

import com.quarkus.api.PersonControllerApi;
import com.quarkus.model.Person;
import com.quarkus.model.PersonCreateRequest;
import com.security.Role;
import com.service.PersonService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import java.util.List;

@Authenticated
public class PersonController implements PersonControllerApi {

    @Inject
    PersonService personService;

    @Inject
    SecurityIdentity identity;

    @RolesAllowed({Role.PERSON_VIEW, Role.PERSON_HOBBY_VIEW})
    @Override
    public Uni<List<Person>> getAllPersons() {
        if (identity.getRoles().contains(Role.PERSON_HOBBY_VIEW)) {
            return personService.getAllPersonsWithHobbies();
        } else {
            return personService.getAllPersons();
        }
    }

    @RolesAllowed({Role.PERSON_VIEW, Role.PERSON_HOBBY_VIEW})
    @Override
    public Uni<List<Person>> getAllPersonsByHobby(String hobby) {
        if (identity.getRoles().contains(Role.PERSON_HOBBY_VIEW)) {
            return personService.getPersonsWithHobbiesByHobby(hobby);
        } else {
            return personService.getPersonsByHobby(hobby);
        }
    }

    @RolesAllowed({Role.PERSON_VIEW, Role.PERSON_HOBBY_VIEW})
    @Override
    public Uni<Person> getPerson(String id) {
        if (identity.getRoles().contains(Role.PERSON_HOBBY_VIEW)) {
            return personService.getPersonWithHobby(id);
        } else {
            return personService.getPerson(id);
        }
    }

    @Override
    public Uni<Person> createPerson(PersonCreateRequest personCreateRequest) {
        return personService.createPerson(personCreateRequest, identity.getPrincipal().getName());
    }

    @Override
    public Uni<Void> deletePerson(String id) {
        return personService.deletePerson(id, identity.getPrincipal().getName());
    }

    @Override
    public Uni<Void> updatePerson(String id, Person person) {
        return personService.updatePerson(id, person, identity.getPrincipal().getName());
    }
}
