package com.iot_edge.managementconsole.dto.user;

import com.iot_edge.managementconsole.dto.system.FirmDTO;
import com.iot_edge.managementconsole.entity.system.Firm;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleDTO {
    private UUID uuid;
    private Integer id;
    private String roleName;
    private Boolean active;
    private FirmDTO firm;
    private Date createdDate;
    private String createdBy;
 }
