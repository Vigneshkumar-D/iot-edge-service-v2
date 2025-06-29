package com.iot_edge.shift.dto;

import com.iot_edge.shift.constants.ShiftStatus;
import com.iot_edge.shift.entity.ShiftBreaks;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ShiftDTO {
    private Long id;

    private String shiftName;

    private Instant startTime;

    private Instant endTime;

    private int totalDuration;

    private ShiftStatus status;

    @Builder.Default
    private List<ShiftBreaks> breaks = new ArrayList<>();

}
