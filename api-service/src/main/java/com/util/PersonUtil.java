package com.util;

import com.google.protobuf.Timestamp;
import com.model.Person;
import grpc.PersonObject;
import grpc.PersonObject.Builder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class PersonUtil {

    private PersonUtil() {

    }

    public static Person getPerson(PersonObject personObject) {
        Instant date = Instant.ofEpochSecond(personObject.getRegistrationDateTimestamp().getSeconds());
        return new Person(personObject.getId(), personObject.getFirstName(), personObject.getLastName(),
            personObject.getAge(), LocalDate.ofInstant(date, ZoneId.systemDefault()));
    }

    public static PersonObject getPersonObject(Person person) {
        Builder builder = PersonObject.newBuilder()
            .setFirstName(person.firstName())
            .setLastName(person.lastName())
            .setAge(person.age());

        Timestamp timestamp = Timestamp.newBuilder()
            .setSeconds(Instant.from(person.registrationDate().atStartOfDay(ZoneId.systemDefault())).getEpochSecond())
            .build();

        builder.setRegistrationDateTimestamp(timestamp);

        if (person.id() != null) {
            builder.setId(person.id());
        }

        return builder.build();
    }
}
