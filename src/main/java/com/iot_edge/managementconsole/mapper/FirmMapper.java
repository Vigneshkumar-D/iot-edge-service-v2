package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.system.AssetDTO;
import com.iot_edge.managementconsole.dto.system.FirmDTO;
import com.iot_edge.managementconsole.entity.system.Asset;
import com.iot_edge.managementconsole.entity.system.Firm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FirmMapper {
    FirmMapper INSTANCE = Mappers.getMapper(FirmMapper.class);

    @Mapping(source = "uuid", target = "uuid")
    FirmDTO toFirmDTO(Firm firm);

    @Mapping(source = "uuid", target = "uuid")
    Firm toFirm(FirmDTO firmDTO);
}
