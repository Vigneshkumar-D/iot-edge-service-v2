package com.iot_edge.shift.entity;

import com.iot_edge.managementconsole.entity.system.Asset;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shift_allocation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id", nullable = false)
    private Asset asset;

    private Integer firmId;

    private Instant shiftDate;

    private Instant startDate;

    private Instant endDate;

    private Integer doneBy;
}
