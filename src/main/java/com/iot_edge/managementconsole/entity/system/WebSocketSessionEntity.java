package com.iot_edge.managementconsole.entity.system;

import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "websocket_sessions")
public class WebSocketSessionEntity extends Auditable<String> implements Serializable {

    @Id
    private String sessionId;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "user_uuid")
    private String userUuid;

    public WebSocketSessionEntity(String sessionId, String userUuid) {
        super();
    }

//    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
