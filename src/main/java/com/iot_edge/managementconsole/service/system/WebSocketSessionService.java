package com.iot_edge.managementconsole.service.system;

import com.iot_edge.managementconsole.entity.system.WebSocketSessionEntity;
import com.iot_edge.managementconsole.repository.system.WebSocketSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebSocketSessionService {

    private final WebSocketSessionRepository sessionRepository;

    public WebSocketSessionService(WebSocketSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public void saveUserSession(String userUuid, String sessionId) {
        WebSocketSessionEntity session = new WebSocketSessionEntity(sessionId, userUuid);
        sessionRepository.save(session);
        System.out.println("Saved session: " + sessionId + " for user: " + userUuid);
    }

    @Transactional
    public void removeUserSession(String userId, String sessionId) {
        sessionRepository.deleteById(sessionId);
        System.out.println("Removed session: " + sessionId + " for user: " + userId);
    }
}
