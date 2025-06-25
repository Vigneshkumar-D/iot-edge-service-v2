package com.iot_edge.managementconsole.dto.user;

import com.iot_edge.managementconsole.entity.user.Role;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {
    private String username;
    private String email;
    private String mobileNumber;
    private Role role;
    private String password;
    private Boolean active;
//    private MultipartFile profileImage;
    private String avatarUrl;
}
