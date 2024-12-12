package com.controller;

import com.person.api.PersonControllerApi;
import com.person.model.Person;
import com.person.model.PersonCreateRequest;
import com.service.PersonService;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class PersonController implements PersonControllerApi {

    @Inject
    PersonService personService;

    @Override
    public CompletionStage<List<Person>> getAllPersons() {
        return personService.getAllPersons().subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<List<Person>> getAllPersonsByHobby(String hobby) {
        return personService.getPersonsByHobby(hobby).subscribeAsCompletionStage();
    }

    @Override
    public CompletionStage<Person> getPerson(String id) {
        return personService.getPerson(id).subscribeAsCompletionStage();
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
