package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.system.AssetDTO;
import com.iot_edge.managementconsole.entity.system.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AssetMapper {
	AssetMapper INSTANCE = Mappers.getMapper(AssetMapper.class);

	@Mapping(source = "uuid", target = "uuid")
	AssetDTO toAssetDTO(Asset asset);

	@Mapping(source = "uuid", target = "uuid")
	Asset toAsset(AssetDTO  assetDTO);
}
