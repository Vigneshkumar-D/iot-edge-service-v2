package com.iot_edge.shift.dto;


import com.iot_edge.shift.entity.Shift;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ShiftBreaksDTO {

    private Long id;

    private String breakName;

    private Instant startTime;

    private Instant endTime;

    private Shift shift;
}
