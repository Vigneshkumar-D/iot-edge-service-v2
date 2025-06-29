package com.iot_edge.shift.entity;

import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shift_breaks")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShiftBreaks extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    private String breakName;   // e.g., "Lunch Break", "Tea Break"

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @ManyToOne
    @JoinColumn(name = "shift_uuid",referencedColumnName = "uuid", nullable = false)
    private Shift shift;
}
