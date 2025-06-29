package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.user.RoleDTO;
import com.iot_edge.managementconsole.entity.user.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(source = "uuid", target = "uuid")
    RoleDTO toRoleDTO(Role role);

    @Mapping(source = "uuid", target = "uuid")
    Role toRole(RoleDTO roleDTO);
}
