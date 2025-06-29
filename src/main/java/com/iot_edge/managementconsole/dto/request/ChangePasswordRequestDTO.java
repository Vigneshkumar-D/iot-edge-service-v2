package com.iot_edge.managementconsole.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
    private String otp;
}