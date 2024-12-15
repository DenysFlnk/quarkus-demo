package com.controller;

import com.person.api.PersonControllerApi;
import com.person.model.Person;
import com.person.model.PersonCreateRequest;
import com.security.Role;
import com.service.PersonService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Authenticated
public class PersonController implements PersonControllerApi {

    @Inject
    PersonService personService;

    @Inject
    SecurityIdentity identity;

    @RolesAllowed({Role.PERSON_VIEW, Role.PERSON_HOBBY_VIEW})
    @Override
    public CompletionStage<List<Person>> getAllPersons() {
        if (identity.getRoles().contains(Role.PERSON_HOBBY_VIEW)) {
            return personService.getAllPersonsWithHobbies().subscribeAsCompletionStage();
        } else {
            return personService.getAllPersons().subscribeAsCompletionStage();
        }
    }

    @RolesAllowed({Role.PERSON_VIEW, Role.PERSON_HOBBY_VIEW})
    @Override
    public CompletionStage<List<Person>> getAllPersonsByHobby(String hobby) {
        if (identity.getRoles().contains(Role.PERSON_HOBBY_VIEW)) {
            return personService.getPersonsWithHobbiesByHobby(hobby).subscribeAsCompletionStage();
        } else {
            return personService.getPersonsByHobby(hobby).subscribeAsCompletionStage();
        }
    }

    @RolesAllowed({Role.PERSON_VIEW, Role.PERSON_HOBBY_VIEW})
    @Override
    public CompletionStage<Person> getPerson(String id) {
        if (identity.getRoles().contains(Role.PERSON_HOBBY_VIEW)) {
            return personService.getPersonWithHobby(id).subscribeAsCompletionStage();
        } else {
            return personService.getPerson(id).subscribeAsCompletionStage();
        }
    }

    @Override
    public CompletionStage<Person> createPerson(PersonCreateRequest personCreateRequest) {
        return personService.createPerson(personCreateRequest).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<Void> deletePerson(String id) {
        return personService.deletePerson(id).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<Void> updatePerson(String id, Person person) {
        return personService.updatePerson(id, person).subscribeAsCompletionStage();
    }
}
