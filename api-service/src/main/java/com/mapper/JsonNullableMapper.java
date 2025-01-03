package com.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper
public interface JsonNullableMapper {

    JsonNullableMapper INSTANCE = Mappers.getMapper(JsonNullableMapper.class);

    default <T> boolean isNotEmpty(JsonNullable<T> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent();
    }
}
