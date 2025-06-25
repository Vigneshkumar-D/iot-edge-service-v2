package com.iot_edge.managementconsole.controller.system;

import com.iot_edge.managementconsole.dto.system.AssetDto;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.service.system.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
public class AssetController {

    private static final Logger log = LoggerFactory.getLogger(AssetController.class);
    @Autowired
    private AssetService assetService;

    @PostMapping
    public ResponseEntity<ResponseModel<?>> add(@RequestBody AssetDto assetDto) {
        log.info("Received request: {}", assetDto);
        return assetService.add(assetDto);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<?>> list() {
        return assetService.list();
    }

    @PutMapping("/{assetId}")
    public ResponseEntity<ResponseModel<?>> update(@PathVariable("assetId") Integer assetId, @RequestBody AssetDto assetDto){
        return assetService.update(assetId, assetDto);
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<ResponseModel<?>> delete(@PathVariable Integer assetId) {
        return assetService.delete(assetId);
    }
}
