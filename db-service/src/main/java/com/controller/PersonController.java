package com.controller;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import com.entity.Person;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;

@Path("/api/persons")
public class PersonController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Person>> getAllPersons() {
        return Person.listAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> getPersonById(Long id) {
        return Person.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Person> createPerson(Person person) {
        return Panache.withTransaction(person::persist);
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> updatePerson(Long id, Person person) {
        return Panache.withTransaction(() -> Person.<Person>findById(id)
            .onItem()
            .ifNotNull()
            .invoke(entity -> {
                entity.setFirstName(person.getFirstName());
                entity.setLastName(person.getLastName());
                entity.setRegistrationDate(person.getRegistrationDate());
                entity.setAge(person.getAge());
            }))
            .onItem()
            .ifNotNull()
            .transform(entity -> Response.ok(entity).build())
            .onItem()
            .ifNull()
            .continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deletePersonById(Long id) {
        return Panache.withTransaction(() -> Person.deleteById(id))
            .map(isDeleted -> isDeleted ? Response.noContent().build() :
                Response.ok().status(Status.NOT_FOUND).build());
    }
}
