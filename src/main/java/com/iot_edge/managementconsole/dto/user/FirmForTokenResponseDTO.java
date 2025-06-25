package com.iot_edge.managementconsole.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FirmForTokenResponseDTO {
    private UUID uuid;
    private String logoUrl;
    private String name;
    private String email;
    private String contactNo;
    private String website;
}
