package com.iot_edge.managementconsole.dto.request;

import com.iot_edge.managementconsole.constants.AssetCategory;
import com.iot_edge.managementconsole.dto.system.FirmDTO;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.model.system.Parameters;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetRequestDTO {
    private UUID uuid;
    private Integer id;
    private String assetName;
    private String scriptCode;
    private AssetCategory assetCategory;
    private String description;
    //    private Location location;
    private FirmDTO firm;
    private String clientId;
    private String subTopicName;
    private String pubTopicName;
    private boolean isActive;
    private List<Parameters> parameters;
}
