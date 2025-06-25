package com.iot_edge.managementconsole.repository.system;

import com.iot_edge.managementconsole.entity.system.WebSocketSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebSocketSessionRepository extends JpaRepository<WebSocketSessionEntity, String> {
    void deleteByUserUuid(String userId);
}
