package com.iot_edge.managementconsole.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PingSuccessResponseDTO {
    private Integer status;
    private String message;
}
