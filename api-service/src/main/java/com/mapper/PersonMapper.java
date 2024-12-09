package com.mapper;

import com.model.Person;
import com.model.PersonDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);
    DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    default String map(LocalDate date) {
        return date != null ? date.format(FORMATTER) : null;
    }

    default LocalDate map(String date) {
        return date != null ? LocalDate.parse(date, FORMATTER) : null;
    }

    PersonDTO toDto(Person person);

    Person toEntity(PersonDTO dto);

    List<PersonDTO> toDtoList(List<Person> persons);

    List<Person> toEntityList(List<PersonDTO> dtos);
}
