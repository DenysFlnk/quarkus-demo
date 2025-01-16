package com.mapper;

import com.entity.Person;
import com.entity.PersonHobby;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import person.PersonList;
import person.PersonObject;
import person_hobby.PersonHobbyCreateRequest;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    uses = {BaseProtoMapper.class, HobbyMapper.class},
    componentModel = MappingConstants.ComponentModel.CDI)
public interface PersonMapper {

    default PersonList toPersonList(List<Person> personList) {
        return PersonList.newBuilder().addAllPerson(personList.stream()
            .map(this::toPersonObject)
            .toList())
            .build();
    }

    @Mapping(target = "registrationDateTimestamp", source = "registrationDate")
    @Mapping(target = "hobbiesList", source = "hobbies")
    PersonObject toPersonObject(Person person);

    @Mapping(target = "registrationDate", source = "registrationDateTimestamp")
    Person toPerson(PersonObject personObject);

    @Mapping(target = "registrationDate", source = "registrationDateTimestamp")
    void updatePerson(@MappingTarget Person person, PersonObject personObject);

    @Mapping(target = "id", ignore = true)
    PersonHobby toPersonHobby(PersonHobbyCreateRequest request);
}
