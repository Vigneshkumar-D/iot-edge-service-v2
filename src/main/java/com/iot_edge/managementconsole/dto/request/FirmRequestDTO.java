package com.iot_edge.managementconsole.dto.request;

import com.iot_edge.managementconsole.dto.system.FirmDTO;
import com.iot_edge.managementconsole.dto.system.LocationDTO;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.system.Location;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirmRequestDTO {

    private String name;

    private String contactNo;

    private String email;

    private String website;

    private String logoUrl;

    private FirmDTO parent;

    private LocationDTO location;
}
