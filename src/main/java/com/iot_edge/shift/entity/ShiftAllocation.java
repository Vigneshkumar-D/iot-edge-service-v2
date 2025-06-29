package com.iot_edge.shift.entity;

import com.iot_edge.common.Auditable;
import com.iot_edge.managementconsole.entity.system.Asset;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shift_allocation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShiftAllocation extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "shift_uuid", referencedColumnName = "uuid", nullable = false)
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "asset_uuid", referencedColumnName = "uuid", nullable = false)
    private Asset asset;

    private Integer firmId;

    private Instant shiftDate;

    private Instant startDate;

    private Instant endDate;

    private Integer doneBy;
}
