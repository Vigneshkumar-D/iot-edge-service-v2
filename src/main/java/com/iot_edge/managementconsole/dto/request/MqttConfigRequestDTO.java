package com.iot_edge.managementconsole.dto.request;


import com.iot_edge.managementconsole.dto.system.AssetDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MqttConfigRequestDTO {
    private AssetDTO asset;
    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
}
