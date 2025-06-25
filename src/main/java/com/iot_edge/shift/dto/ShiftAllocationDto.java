package com.iot_edge.shift.dto;

import com.iot_edge.managementconsole.entity.system.Asset;
import com.iot_edge.shift.entity.Shift;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAllocationDto {

    private Long id;

    private Shift shift;

    private List<Asset> assets;

    private Integer firmId;

    private Instant shiftDate;

    private Instant startDate;

    private Instant endDate;

    private Integer doneBy;
}
