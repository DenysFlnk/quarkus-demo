package com.util;

import com.entity.Person;
import com.google.protobuf.Timestamp;
import grpc.PersonObject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class PersonUtil {

    private PersonUtil() {

    }

    public static PersonObject getProtoPersonObject(Person person) {
        Instant date = Instant.from(person.getRegistrationDate().atStartOfDay(ZoneId.systemDefault()));
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(date.getEpochSecond()).build();

        return PersonObject.newBuilder().setId(person.getId())
            .setFirstName(person.getFirstName())
            .setLastName(person.getLastName())
            .setAge(person.getAge())
            .setRegistrationDateTimestamp(timestamp)
            .build();
    }

    public static Person getPerson(PersonObject personObject) {
        Person person = new Person();
        return updatePerson(person, personObject);
    }

    public static Person updatePerson(Person person, PersonObject personObject) {
        person.setFirstName(personObject.getFirstName());
        person.setLastName(personObject.getLastName());
        person.setAge(personObject.getAge());

        Instant date = Instant.ofEpochSecond(personObject.getRegistrationDateTimestamp().getSeconds());
        person.setRegistrationDate(LocalDate.ofInstant(date, ZoneId.systemDefault()));

        return person;
    }
}
