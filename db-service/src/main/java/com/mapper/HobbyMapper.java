package com.mapper;

import com.entity.Hobby;
import hobby.HobbyObject;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HobbyMapper {

    HobbyMapper INSTANCE = Mappers.getMapper(HobbyMapper.class);

    Hobby toHobby(HobbyObject hobbyObject);
}
