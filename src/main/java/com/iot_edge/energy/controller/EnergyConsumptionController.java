package com.iot_edge.energy.controller;

import com.iot_edge.energy.service.EnergyConsumptionService;
import com.iot_edge.managementconsole.entity.system.Asset;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/energy")
public class EnergyConsumptionController {

    @Autowired
    private EnergyConsumptionService energyConsumptionService;

    @GetMapping
    public ResponseEntity<ResponseModel<?>> list(
            @RequestParam(required = false, name = "assetId") Integer assetId,
            @RequestParam(required = false, name = "startDate") String startDate,
            @RequestParam(required = false, name = "endDate") String endDate,
            @RequestParam(required = false, name = "minPowerFactor") Double minPowerFactor,
            @RequestParam(required = false, name = "maxPowerFactor") Double maxPowerFactor) {

        Asset asset = assetId != null ? new Asset() : null;
        if (asset != null) asset.setId(assetId);

        Instant start = (startDate != null) ? Instant.parse(startDate) : null;
        Instant end = (endDate != null) ? Instant.parse(endDate) : null;

        return energyConsumptionService.list(assetId, start, end, minPowerFactor, maxPowerFactor);
    }
}
