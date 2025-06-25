package com.iot_edge.managementconsole.websocket;

import com.iot_edge.managementconsole.service.system.WebSocketSessionService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final WebSocketSessionService sessionService;

    public WebSocketEventListener(WebSocketSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        String userId = extractUserId(event);
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();

        System.out.println("STOMP Connection Established: " + sessionId);
        sessionService.saveUserSession(userId, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        System.out.println("STOMP Disconnected: " + sessionId);
        sessionService.removeUserSession("defaultUser", sessionId);
    }

    private String extractUserId(SessionConnectEvent event) {
        return event.getUser() != null ? event.getUser().getName() : "unknown";
    }
}
