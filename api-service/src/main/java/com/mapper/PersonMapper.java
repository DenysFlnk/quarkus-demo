package com.mapper;

import com.google.protobuf.Timestamp;
import com.person.model.Person;
import com.person.model.PersonCreateRequest;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import person.PersonList;
import person.PersonObject;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    default Timestamp map(Long seconds) {
        return Timestamp.newBuilder().setSeconds(seconds).build();
    }

    default Long map(Timestamp timestamp) {
        return timestamp.getSeconds();
    }

    default List<Person> toPersonList(PersonList personList) {
        List<Person> list = personList.getPersonList()
            .stream()
            .map(this::toPerson)
            .toList();
        return list;
    }

    @Mapping(target = "hobbies", source = "hobbiesList")
    Person toPerson(PersonObject personObject);

    @Mapping(target = "hobbiesList", source = "hobbies")
    PersonObject toPersonObject(Person person);

    PersonObject toPersonObject(PersonCreateRequest personCreateRequest);
}
