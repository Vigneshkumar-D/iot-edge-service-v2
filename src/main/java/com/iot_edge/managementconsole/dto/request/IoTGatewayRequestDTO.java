package com.iot_edge.managementconsole.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IoTGatewayRequestDTO {
    private String serverName;
    private String brokerUrl;
    private Integer serverPort;
    private String clientId;
    private String userName;
    private String password;
    private Boolean status;
}
