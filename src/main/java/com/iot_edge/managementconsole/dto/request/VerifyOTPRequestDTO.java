package com.iot_edge.managementconsole.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOTPRequestDTO implements Serializable {
    private String email;
    private String otp;
    private String type;
}
