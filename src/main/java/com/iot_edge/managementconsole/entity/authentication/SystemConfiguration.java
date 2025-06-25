package com.iot_edge.managementconsole.entity.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@Table(name = "system_configuration")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE system_configuration SET is_deleted = true where id=?")
@SQLRestriction("is_deleted = false")
public class SystemConfiguration extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    private String key;

    private String value;

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

