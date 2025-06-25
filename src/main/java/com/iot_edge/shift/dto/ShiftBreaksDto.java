package com.iot_edge.shift.dto;


import com.iot_edge.shift.entity.Shift;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftBreaksDto {

    private Long id;

    private String breakName;

    private Instant startTime;

    private Instant endTime;

    private Shift shift;
}
