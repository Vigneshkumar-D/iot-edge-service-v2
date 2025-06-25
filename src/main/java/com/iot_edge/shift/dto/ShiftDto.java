package com.iot_edge.shift.dto;

import com.iot_edge.shift.constants.ShiftStatus;
import com.iot_edge.shift.entity.ShiftBreaks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftDto {
    private Long id;

    private String shiftName;

    private Instant startTime;

    private Instant endTime;

    private int totalDuration;

    private ShiftStatus status;

    private List<ShiftBreaks> breaks = new ArrayList<>();

}
