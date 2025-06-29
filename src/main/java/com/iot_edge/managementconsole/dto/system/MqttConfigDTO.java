package com.iot_edge.managementconsole.dto.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MqttConfigDTO {
    private UUID uuid;
    private Integer id;
    private AssetDTO asset;
    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
}
