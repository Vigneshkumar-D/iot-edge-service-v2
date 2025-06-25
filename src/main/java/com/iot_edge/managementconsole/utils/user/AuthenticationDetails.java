package com.iot_edge.managementconsole.utils.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AuthenticationDetails implements Serializable {
    private String name;
    private String email;
    private String contactNo;
    private String uuid;
    private String roles;
    private String organizationUuid;
}
