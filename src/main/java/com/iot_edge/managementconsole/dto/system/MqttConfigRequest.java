package com.iot_edge.managementconsole.dto.system;

import com.iot_edge.managementconsole.entity.system.Asset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MqttConfigRequest {
    private Asset asset;
    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
}
