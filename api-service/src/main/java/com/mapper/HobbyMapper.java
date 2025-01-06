package com.mapper;

import com.quarkus.model.Hobby;
import hobby.HobbyList;
import hobby.HobbyObject;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface HobbyMapper {

    HobbyMapper INSTANCE = Mappers.getMapper(HobbyMapper.class);

    default List<Hobby> toHobbyList(HobbyList hobbyList) {
        return hobbyList.getHobbiesList().stream()
            .map(this::toHobby)
            .toList();
    }

    Hobby toHobby(HobbyObject hobbyObject);

    HobbyObject toHobbyObject(Hobby hobby);
}
