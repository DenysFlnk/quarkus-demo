package com.mapper;

import com.entity.Hobby;
import com.google.protobuf.StringValue;
import hobby.HobbyList;
import hobby.HobbyObject;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HobbyMapper {

    HobbyMapper INSTANCE = Mappers.getMapper(HobbyMapper.class);

    default HobbyList toHobbyList(List<Hobby> hobbies) {
        return HobbyList.newBuilder()
            .addAllHobbies(hobbies.stream()
                .map(INSTANCE::toHobbyObject)
                .toList())
            .build();
    }

    default Hobby toHobby(StringValue name) {
        Hobby hobby = new Hobby();
        hobby.setName(name.getValue());
        return hobby;
    }

    Hobby toHobby(HobbyObject hobbyObject);

    HobbyObject toHobbyObject(Hobby hobby);

    void updateHobby(@MappingTarget Hobby hobby, HobbyObject hobbyObject);
}
