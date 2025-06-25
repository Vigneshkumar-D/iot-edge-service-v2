package com.iot_edge.managementconsole.entity.system;


import com.iot_edge.common.Auditable;
import com.iot_edge.managementconsole.model.system.Parameters;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "alert_log")
public class AlertLog extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    private String alertName;

    private Parameters parameters;

    private String description;

    private Integer priority;

    @ManyToOne
    @JoinColumn(name = "asset_uuid", referencedColumnName = "uuid")
    private Asset asset;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
