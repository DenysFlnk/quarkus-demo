package com.mapper;

import com.google.protobuf.Timestamp;
import com.quarkus.model.Hobby;
import com.quarkus.model.Person;
import com.quarkus.model.PersonCreateRequest;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import person.DeletePersonRequest;
import person.PersonList;
import person.PersonObject;
import person_hobby.PersonHobbyCreateRequest;
import person_hobby.PersonHobbyDeleteRequest;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    componentModel =  MappingConstants.ComponentModel.JAKARTA_CDI)
public interface PersonMapper {

    default Timestamp map(Long seconds) {
        return Timestamp.newBuilder().setSeconds(seconds).build();
    }

    default Long map(Timestamp timestamp) {
        return timestamp.getSeconds();
    }

    default List<Person> toPersonList(PersonList personList) {
        return personList.getPersonList()
            .stream()
            .map(this::toPerson)
            .toList();
    }

    @Mapping(target = "hobbies", source = "hobbiesList")
    Person toPerson(PersonObject personObject);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hobbiesList", source = "person.hobbies")
    @Mapping(target = "author", source = "author")
    PersonObject toPersonObject(String id, Person person, String author);

    @Mapping(target = "author", source = "author")
    PersonObject toPersonObject(PersonCreateRequest personCreateRequest, String author);

    DeletePersonRequest toDeletePersonRequest(String id, String author);

    PersonHobbyCreateRequest toPersonHobbyCreateRequest(String personId, Hobby hobby, String author);

    PersonHobbyDeleteRequest toPersonHobbyDeleteRequest(Integer personHobbyId, String author);
}
