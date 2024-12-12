package com.mapper;

import com.entity.Person;
import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import person.PersonList;
import person.PersonObject;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    default Timestamp map(LocalDate localDate) {
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).build();
    }

    default LocalDate map(Timestamp timestamp) {
        Instant date = Instant.ofEpochSecond(timestamp.getSeconds());
        return LocalDate.ofInstant(date, ZoneId.systemDefault());
    }

    default PersonList toPersonList(List<Person> personList) {
        return PersonList.newBuilder().addAllPerson(personList.stream()
            .map(INSTANCE::toPersonObject)
            .toList())
            .build();
    }

    @Mapping(target = "registrationDateTimestamp", source = "registrationDate")
    @Mapping(target = "hobbiesList", source = "hobbies")
    PersonObject toPersonObject(Person person);

    @Mapping(target = "registrationDate", source = "registrationDateTimestamp")
    @Mapping(target = "hobbies", source = "hobbiesList")
    Person toPerson(PersonObject personObject);

    @Mapping(target = "registrationDate", source = "registrationDateTimestamp")
    @Mapping(target = "hobbies", source = "hobbiesList")
    void updatePerson(@MappingTarget Person person, PersonObject personObject);
}
