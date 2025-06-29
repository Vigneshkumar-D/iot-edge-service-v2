package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.system.AlertLogDTO;
import com.iot_edge.managementconsole.entity.system.AlertLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlertLogMapper {
    AlertLogMapper INSTANCE = Mappers.getMapper(AlertLogMapper.class);

    @Mapping(source = "uuid", target = "uuid")
    AlertLogDTO toAlertLogDTO(AlertLog alertLog);

    @Mapping(source = "uuid", target = "uuid")
    AlertLog toAlertLog(AlertLog alertLog);
}
