package com.mapper;

import com.google.protobuf.Timestamp;
import com.person.model.Person;
import com.person.model.PersonCreateRequest;
import grpc.PersonId;
import grpc.PersonObject;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    default Timestamp map(Long seconds) {
        return Timestamp.newBuilder().setSeconds(seconds).build();
    }

    default Long map(Timestamp timestamp) {
        return timestamp.getSeconds();
    }

    Person toPerson(PersonObject personObject);

    PersonObject toPersonObject(Person person);

    PersonObject toPersonObject(PersonCreateRequest personCreateRequest);

    PersonId toPersonId(String id);
}
