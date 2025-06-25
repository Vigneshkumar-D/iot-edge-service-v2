package com.iot_edge.shift.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shift_breaks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftBreaks {
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
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;
}
