package com.iot_edge.managementconsole.dto.system;

import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.system.Location;
//import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
//@Server
public class FirmDto {

    private String name;

    private Firm parent;

    private Location location;

}
