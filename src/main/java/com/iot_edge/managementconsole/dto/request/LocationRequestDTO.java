package com.iot_edge.managementconsole.dto.request;

import com.iot_edge.managementconsole.dto.system.LocationDTO;
import com.iot_edge.managementconsole.entity.system.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationRequestDTO {
    private String locationName;
    private String category;
    private LocationDTO parent;
    private String latitude;
    private String longitude;
}
