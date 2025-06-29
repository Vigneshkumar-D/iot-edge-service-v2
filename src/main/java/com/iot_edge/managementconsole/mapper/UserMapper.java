package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.user.UserDTO;
import com.iot_edge.managementconsole.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "uuid", target = "uuid")
    UserDTO toUserDTO(User user);

    @Mapping(source = "uuid", target = "uuid")
    User toUser(User user);
}
