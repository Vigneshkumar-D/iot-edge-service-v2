package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.system.LocationDTO;
import com.iot_edge.managementconsole.entity.system.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    @Mapping(source = "uuid", target = "uuid")
    LocationDTO toLocationDTO(Location location);

    @Mapping(source = "uuid", target = "uuid")
    Location toLocation(LocationDTO locationDTO);
}
