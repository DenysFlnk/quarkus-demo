package com.controller;

import com.PersonService;
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
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> getPersonById(Long id) {
        return personService.getPerson(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> createPerson(Person person) {
        throw new UnsupportedOperationException();
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> updatePerson(Long id, Person person) {
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deletePersonById(Long id) {
        throw new UnsupportedOperationException();
    }
}
