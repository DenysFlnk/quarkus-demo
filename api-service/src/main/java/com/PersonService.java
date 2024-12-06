package com;

import com.model.Person;
import grpc.Empty;
import grpc.PersonId;
import grpc.PersonObject;
import grpc.PersonObject.Builder;
import grpc.PersonProtoService;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PersonService {

    @GrpcClient("person-service")
    PersonProtoService personGrpcService;

    public Uni<Person> getPerson(long id) {
        return personGrpcService.getPerson(PersonId.newBuilder().setId(id).build())
            .onItem()
            .transform(PersonService::getPerson);
    }

    public Uni<List<Person>> getAllPersons() {
        return personGrpcService.getAllPersons(Empty.getDefaultInstance())
            .onItem()
            .transform(personList -> personList.getPersonList().stream()
                .map(PersonService::getPerson)
                .toList());
    }

    public Uni<Person> createPerson(Person person) {
        return personGrpcService.createPerson(getPersonObject(person))
            .onItem()
            .transform(PersonService::getPerson);
    }

    public Uni<Empty> updatePerson(Person person) {
        return personGrpcService.updatePerson(getPersonObject(person));
    }

    public Uni<Empty> deletePerson(long id) {
        return personGrpcService.deletePerson(PersonId.newBuilder().setId(id).build());
    }

    private static Person getPerson(PersonObject response) {
        return new Person(response.getId(), response.getFirstName(), response.getLastName(),
            response.getAge(), LocalDate.ofEpochDay(response.getRegistrationDateInEpochDays()));
    }

    private static PersonObject getPersonObject(Person person) {
        System.out.println(person.registrationDate().toEpochDay());
        Builder builder = PersonObject.newBuilder()
            .setFirstName(person.firstName())
            .setLastName(person.lastName())
            .setAge(person.age())
            .setRegistrationDateInEpochDays(person.registrationDate().toEpochDay());

        if (person.id() != null) {
            builder.setId(person.id());
        }

        return builder.build();
    }
}
