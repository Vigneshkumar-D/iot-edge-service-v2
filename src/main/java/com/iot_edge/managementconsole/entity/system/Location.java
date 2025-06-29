package com.iot_edge.managementconsole.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "location")
public class Location extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    private String locationName;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Location parent;

    private String category;

    private String latitude;

    private String longitude;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
