package com;

import com.model.Person;
import grpc.PersonProtoService;
import grpc.PersonRequest;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class PersonService {

    @GrpcClient("person-service")
    PersonProtoService personService;

    public Uni<Person> getPerson(long id) {
        return personService.getPerson(PersonRequest.newBuilder().setId(id).build())
            .onItem()
            .transform(response -> new Person(response.getId(), response.getFirstName(), response.getLastName(),
                response.getAge(), LocalDate.ofEpochDay(response.getRegistrationDateInEpochDays())));
    }
}
