package com.iot_edge.energy.entity;

import com.iot_edge.common.Auditable;
import com.iot_edge.managementconsole.entity.system.Asset;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "energy_consumption")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)// Enables caching
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnergyConsumption extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "asset_uuid", referencedColumnName = "uuid", nullable = false)
    private Asset asset;

    @Column(name = "power_factor")
    private Double powerFactor;

    @Column(name = "voltage")
    private Double voltage;

    @Column(name = "current")
    private Double current;

    @Column(name = "frequency")
    private Double frequency;

    @Column(name = "meter_reading")
    private Double meterReading;

    @Column(name = "today_consumption")
    private Double todayConsumption;

    @Column(name = "monthly_consumption")
    private Double monthlyConsumption;

    @Column(name = "cumulative")
    private Double cumulative;

    @Column(name = "timestamp")
    private Instant timestamp;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }

}
