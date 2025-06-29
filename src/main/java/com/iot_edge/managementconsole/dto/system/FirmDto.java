package com.iot_edge.managementconsole.dto.system;

import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.system.Location;
//import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FirmDTO {

    private Integer id;
    private UUID uuid;
    private String name;
    private String contactNo;
    private String email;
    private String website;
    private String logoUrl;
    private FirmDTO parent;
    private LocationDTO location;

}
