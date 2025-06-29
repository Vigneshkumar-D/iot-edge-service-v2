package com.iot_edge.shift.entity;

import com.iot_edge.common.Auditable;
import com.iot_edge.shift.constants.ShiftStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shifts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Shift extends Auditable<String> implements Serializable {
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
    private ShiftStatus status = ShiftStatus.SCHEDULED;

    @Builder.Default
    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftBreaks> breaks = new ArrayList<>();

}
