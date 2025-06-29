package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.user.UserTokenDTO;
import com.iot_edge.managementconsole.entity.user.UserToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserTokenMapper {
    UserTokenMapper INSTANCE = Mappers.getMapper(UserTokenMapper.class);

    @Mapping(source = "uuid", target = "uuid")
    UserTokenDTO toUserTokenDTO(UserToken userToken);

    @Mapping(source = "uuid", target = "uuid")
    UserToken toUserToken(UserToken userToken);
}
