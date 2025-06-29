package com.iot_edge.managementconsole.entity.system;

import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "websocket_sessions")
public class WebSocketSession extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    private String sessionId;

    @Column(name = "user_uuid")
    private String userUuid;

    public WebSocketSession(String sessionId, String userUuid) {
        super();
    }

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
