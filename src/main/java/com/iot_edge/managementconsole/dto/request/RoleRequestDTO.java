package com.iot_edge.managementconsole.dto.request;

import com.iot_edge.managementconsole.dto.system.FirmDTO;
import com.iot_edge.managementconsole.entity.system.Firm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleRequestDTO {
    private String roleName;
    private Boolean active;
    private FirmDTO firm;
}
