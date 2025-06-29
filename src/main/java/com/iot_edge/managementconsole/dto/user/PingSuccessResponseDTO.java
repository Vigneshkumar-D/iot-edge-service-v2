package com.iot_edge.managementconsole.dto.user;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PingSuccessResponseDTO {
    private Integer status;
    private String message;
}
