package com.mapper;

import com.entity.Hobby;
import com.google.protobuf.StringValue;
import hobby.HobbyCreateRequest;
import hobby.HobbyList;
import hobby.HobbyObject;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface HobbyMapper {

    default HobbyList toHobbyList(List<Hobby> hobbies) {
        return HobbyList.newBuilder()
            .addAllHobbies(hobbies.stream()
                .map(this::toHobbyObject)
                .toList())
            .build();
    }

    @Mapping(target = "id", ignore = true)
    Hobby toHobby(HobbyCreateRequest request);

    HobbyObject toHobbyObject(Hobby hobby);

    void updateHobby(@MappingTarget Hobby hobby, HobbyObject hobbyObject);
}
