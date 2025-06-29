package com.iot_edge.energy.dto;

import com.iot_edge.managementconsole.entity.system.Asset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnergyConsumptionDto {
    private UUID uuid;
    private Integer id;
    private Asset asset;
    private Double powerFactor;
    private Double voltage;
    private Double current;
    private Double frequency;
    private Double meterReading;
    private Double todayConsumption;
    private Double monthlyConsumption;
    private Double cumulative;
    private Instant timestamp;

    public EnergyConsumptionDto(Asset asset, Double meterReading, Double current, Double voltage, Double frequency, Double powerFactor, Instant timestamp) {
        this.asset = asset;
        this.meterReading = meterReading;
        this.current = current;
        this.voltage = voltage;
        this.frequency = frequency;
        this.powerFactor = powerFactor;
        this.timestamp = timestamp;
    }
}
