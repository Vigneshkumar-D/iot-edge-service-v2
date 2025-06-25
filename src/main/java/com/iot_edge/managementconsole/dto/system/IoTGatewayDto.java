package com.iot_edge.managementconsole.dto.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IoTGatewayDto {
    private String serverName;
    private Integer serverPort;
    private String clientId;
    private String userName;
    private String password;
    private Boolean status;
}

