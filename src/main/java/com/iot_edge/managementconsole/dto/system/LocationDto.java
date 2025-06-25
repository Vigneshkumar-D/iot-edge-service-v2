package com.iot_edge.managementconsole.dto.system;

import com.iot_edge.managementconsole.entity.system.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationDto {

    private String locationName;

    private String category;

    private Location parent;

    private String latitude;

    private String longitude;
}
