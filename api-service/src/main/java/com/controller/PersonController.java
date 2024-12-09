package com.controller;

import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

import com.service.PersonService;
import com.model.Person;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/persons")
public class PersonController {

    @Inject
    PersonService personService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Person>> getAllPersons() {
        return personService.getAllPersons();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> getPersonById(String id) {
        return personService.getPerson(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> createPerson(Person person) {
        return personService.createPerson(person);
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> updatePerson(String id, Person person) {
        return personService.updatePerson(id, person)
            .onItem()
            .transform(item -> Response.ok().status(NO_CONTENT).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deletePersonById(String id) {
        return personService.deletePerson(id)
            .onItem()
            .transform(item -> Response.ok().status(NO_CONTENT).build());
    }
}
