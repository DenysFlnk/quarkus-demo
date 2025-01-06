package com.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import shopping_mall.Location;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    Location toLocation(com.quarkus.model.Location location);

    com.quarkus.model.Location toLocation(Location location);
}
