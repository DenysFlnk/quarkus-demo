package com.mapper;

import com.entity.Person;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import person.PersonList;
import person.PersonObject;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE, uses = BaseProtoMapper.class,
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
    @Mapping(target = "hobbies", source = "hobbiesList")
    Person toPerson(PersonObject personObject);

    @Mapping(target = "registrationDate", source = "registrationDateTimestamp")
    @Mapping(target = "hobbies", source = "hobbiesList")
    void updatePerson(@MappingTarget Person person, PersonObject personObject);
}
