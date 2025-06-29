package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.system.IoTGatewayDTO;
import com.iot_edge.managementconsole.entity.system.IoTGateway;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IoTGatewayMapper {
    IoTGatewayMapper INSTANCE = Mappers.getMapper(IoTGatewayMapper.class);

    @Mapping(source = "uuid", target = "uuid")
    IoTGatewayDTO toIoTGatewayDTO(IoTGateway ioTGateway);

    @Mapping(source = "uuid", target = "uuid")
    IoTGateway toIoTGateway(IoTGatewayDTO ioTGatewayDTO);
}
