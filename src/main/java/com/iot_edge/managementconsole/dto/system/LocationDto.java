package com.iot_edge.managementconsole.dto.system;

import com.iot_edge.managementconsole.entity.system.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationDTO {
    private UUID uuid;
    private Integer id;
    private String locationName;
    private String category;
    private LocationDTO parent;
    private String latitude;
    private String longitude;
    private Date createdDate;
    private String createdBy;
}
