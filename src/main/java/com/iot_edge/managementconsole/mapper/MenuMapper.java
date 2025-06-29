package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.user.MenuDTO;
import com.iot_edge.managementconsole.entity.user.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MenuMapper {
    MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);

    @Mapping(source = "uuid", target = "uuid")
    MenuDTO toMenuDTO(Menu menu);

    @Mapping(source = "uuid", target = "uuid")
    Menu toMenu(Menu menu);
}
