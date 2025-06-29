package com.iot_edge.managementconsole.dto.request;

import com.iot_edge.managementconsole.dto.user.RoleDTO;
import com.iot_edge.managementconsole.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {
    private String username;
    private String email;
    private String mobileNumber;
    private RoleDTO role;
    private String password;
    private Boolean active;
    //    private MultipartFile profileImage;
    private String avatarUrl;
}
