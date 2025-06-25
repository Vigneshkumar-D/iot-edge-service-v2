package com.iot_edge.energy.repository;

import com.iot_edge.energy.entity.EnergyConsumption;
import com.iot_edge.managementconsole.entity.system.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface EnergyConsumptionRepository extends JpaRepository<EnergyConsumption, Long>, JpaSpecificationExecutor<EnergyConsumption> {
    EnergyConsumption findByAssetAndTimestampBetween(Asset asset, Instant startOfDay, Instant endOfDay);
}
