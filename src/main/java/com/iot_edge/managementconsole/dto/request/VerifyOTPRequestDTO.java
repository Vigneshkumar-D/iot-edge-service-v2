package com.iot_edge.managementconsole.dto.request;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerifyOTPRequestDTO implements Serializable {
    private String email;
    private String otp;
    private String type;
}
