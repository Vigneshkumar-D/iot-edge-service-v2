package com.iot_edge.shift.dto;

import com.iot_edge.managementconsole.entity.system.Asset;
import com.iot_edge.shift.entity.Shift;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ShiftAllocationDTO {

    private UUID uuid;
    private Long id;
    private Shift shift;
    private List<Asset> assets;
    private Integer firmId;
    private Instant shiftDate;
    private Instant startDate;
    private Instant endDate;
    private Integer doneBy;
}
