package com.util;

import com.google.protobuf.Timestamp;
import com.person.model.Person;
import com.person.model.PersonCreateRequest;
import grpc.PersonObject;
import grpc.PersonObject.Builder;

public class PersonUtil {

    private PersonUtil() {

    }

    public static Person getPerson(PersonObject personObject) {
        Person person = new Person();
        person.setId(personObject.getId());
        person.setFirstName(personObject.getFirstName());
        person.setLastName(personObject.getLastName());
        person.setAge(personObject.getAge());
        person.setRegistrationDateTimestamp(personObject.getRegistrationDateTimestamp().getSeconds());

        return person;
    }

    public static Person getPerson(PersonCreateRequest request) {
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setAge(request.getAge());
        person.setRegistrationDateTimestamp(request.getRegistrationDateTimestamp());
        return person;
    }

    public static PersonObject getPersonObject(Person person) {
        Builder builder = PersonObject.newBuilder()
            .setFirstName(person.getFirstName())
            .setLastName(person.getLastName())
            .setAge(person.getAge());

        Timestamp timestamp = Timestamp.newBuilder()
            .setSeconds(person.getRegistrationDateTimestamp())
            .build();

        builder.setRegistrationDateTimestamp(timestamp);

        if (person.getId() != null) {
            builder.setId(person.getId());
        }

        return builder.build();
    }
}
