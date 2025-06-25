package com.iot_edge.managementconsole.entity.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.iot_edge.common.Auditable;
import com.iot_edge.managementconsole.config.serializer.LocalDateTimeDeserializer;
import com.iot_edge.managementconsole.constants.NotificationChannel;
import com.iot_edge.managementconsole.constants.NotificationStatus;
import com.iot_edge.managementconsole.constants.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@Table(name = "notifications")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", columnDefinition = "uuid", nullable = false)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name="channel")
    private NotificationChannel channel;

    @Column(name = "scheduled_on")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime scheduledOn;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String recipient;

    @Column(columnDefinition = "TEXT")
    private String subject;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}

