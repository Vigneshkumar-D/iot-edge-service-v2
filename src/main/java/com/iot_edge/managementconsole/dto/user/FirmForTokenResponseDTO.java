package com.iot_edge.managementconsole.dto.user;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FirmForTokenResponseDTO {
    private UUID uuid;
    private String logoUrl;
    private String name;
    private String email;
    private String contactNo;
    private String website;
}
