package com.mapper;

import com.entity.Hobby;
import com.entity.OperationalStatus;
import com.google.protobuf.StringValue;
import hobby.HobbyList;
import hobby.HobbyObject;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import shopping_mall.UpdateStatusRequest;

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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(name.getValue())")
    Hobby toHobby(StringValue name);

    Hobby toHobby(HobbyObject hobbyObject);

    HobbyObject toHobbyObject(Hobby hobby);

    void updateHobby(@MappingTarget Hobby hobby, HobbyObject hobbyObject);
}
