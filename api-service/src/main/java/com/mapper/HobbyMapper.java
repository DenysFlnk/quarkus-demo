package com.mapper;

import com.person.model.Hobby;
import hobby.HobbyList;
import hobby.HobbyObject;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HobbyMapper {

    HobbyMapper INSTANCE = Mappers.getMapper(HobbyMapper.class);

    default List<Hobby> toHobbyList(HobbyList hobbyList) {
        return hobbyList.getHobbiesList().stream()
            .map(INSTANCE::toHobby)
            .toList();
    }

    Hobby toHobby(HobbyObject hobbyObject);

    HobbyObject toHobbyObject(Hobby hobby);
}
