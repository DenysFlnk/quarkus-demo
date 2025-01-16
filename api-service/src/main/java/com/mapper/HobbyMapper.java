package com.mapper;

import com.quarkus.model.Hobby;
import hobby.HobbyCreateRequest;
import hobby.HobbyDeleteRequest;
import hobby.HobbyList;
import hobby.HobbyObject;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface HobbyMapper {

    default List<Hobby> toHobbyList(HobbyList hobbyList) {
        return hobbyList.getHobbiesList().stream()
            .map(this::toHobby)
            .toList();
    }

    Hobby toHobby(HobbyObject hobbyObject);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "author", source = "author")
    HobbyObject toHobbyObject(Integer id, Hobby hobby, String author);

    HobbyCreateRequest toHobbyCreateRequest(com.quarkus.model.HobbyCreateRequest request, String author);

    HobbyDeleteRequest toHobbyDeleteRequest(Integer id, String author);
}
