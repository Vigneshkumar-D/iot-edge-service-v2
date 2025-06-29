package com.iot_edge.managementconsole.mapper;

import com.iot_edge.managementconsole.dto.system.WebSocketSessionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.socket.WebSocketSession;

@Mapper
public interface WebSocketSessionMapper {
    WebSocketSession INSTANCE = Mappers.getMapper(WebSocketSession.class);

    @Mapping(source = "uuid", target = "uuid")
    WebSocketSessionDTO toWebSocketSessionDTO(WebSocketSession webSocketSession);

    @Mapping(source = "uuid", target = "uuid")
    WebSocketSession toWebSocketSession(WebSocketSessionDTO webSocketSessionDTO);
}
