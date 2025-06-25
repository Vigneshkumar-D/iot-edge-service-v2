package com.iot_edge.managementconsole.controller.system;

import com.iot_edge.managementconsole.dto.system.LocationDto;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.service.system.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping
    public ResponseEntity<ResponseModel<?>> add(@RequestBody LocationDto locationDto) {
        return locationService.add(locationDto);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<?>> list() {
        return locationService.list();
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<ResponseModel<?>> update(@PathVariable("locationId") Integer locationId, @RequestBody LocationDto locationDto){
        return locationService.update(locationId, locationDto);
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<ResponseModel<?>> delete(@PathVariable Integer locationId) {
        return locationService.delete(locationId);
    }
}
