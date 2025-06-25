package com.iot_edge.shift.entity;

import com.iot_edge.shift.constants.ShiftStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shifts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    private String shiftName;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    private int totalDuration;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 50, columnDefinition = "varchar(50)")
    private ShiftStatus status = ShiftStatus.Scheduled;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftBreaks> breaks = new ArrayList<>();

}
