package com.iot_edge.managementconsole.dto.system;

import com.iot_edge.managementconsole.constants.AssetCategory;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.model.system.Parameters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssetDTO {
    private UUID uuid;
    private Integer id;
    private String assetName;
    private String scriptCode;
    private AssetCategory assetCategory;
    private String description;
//    private Location location;
    private Firm firm;
    private String clientId;
    private String subTopicName;
    private String pubTopicName;
    private boolean isActive;
    private List<Parameters> parameters;
    private Date createdDate;
    private String createdBy;
}
