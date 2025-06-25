package com.iot_edge.managementconsole.dto.user;

import com.iot_edge.managementconsole.entity.system.Firm;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleDTO {
    private String roleName;
    private Boolean active;
    private Firm firm;
}
