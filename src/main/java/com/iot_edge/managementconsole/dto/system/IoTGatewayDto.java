package com.iot_edge.managementconsole.dto.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IoTGatewayDTO {
    private UUID uuid;
    private Integer id;
    private String brokerUrl;
    private String serverName;
    private Integer serverPort;
    private String clientId;
    private String userName;
    private String password;
    private Date createdDate;
    private String createdBy;
    private Boolean status;
}

