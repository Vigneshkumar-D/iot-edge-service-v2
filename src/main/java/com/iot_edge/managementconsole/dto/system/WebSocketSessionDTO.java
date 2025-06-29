package com.iot_edge.managementconsole.dto.system;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketSessionDTO {
    private String sessionId;
    private UUID uuid;
    private String userUuid;
}
