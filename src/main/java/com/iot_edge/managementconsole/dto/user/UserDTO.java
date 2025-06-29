package com.iot_edge.managementconsole.dto.user;

import com.iot_edge.managementconsole.entity.user.Role;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private UUID uuid;
    private Integer id;
    private String username;
    private String email;
    private String mobileNumber;
    private RoleDTO role;
    private String password;
    private Boolean active;
//    private MultipartFile profileImage;
    private String avatarUrl;
    private Date createdDate;
    private String createdBy;
}
