package com.iot_edge.shift.controller;

import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.shift.dto.ShiftAllocationDTO;
import com.iot_edge.shift.service.ShiftAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shift-allocation")
public class ShiftAllocationController {

    @Autowired
    private ShiftAllocationService shiftAllocationService;

    @GetMapping
    public ResponseEntity<ResponseModel<?>> list() {
        return shiftAllocationService.list();
    }

    @PostMapping
    public ResponseEntity<ResponseModel<?>> createRole(@RequestBody ShiftAllocationDTO shiftAllocationDto) {
        return shiftAllocationService.add(shiftAllocationDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> updateRole(@PathVariable(name = "id") Long shiftAllocationId, @RequestBody ShiftAllocationDTO shiftAllocationDto) {
        return shiftAllocationService.update(shiftAllocationId, shiftAllocationDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> deleteRole(@PathVariable(name = "id") Long shiftId) {
        return shiftAllocationService.delete(shiftId);
    }

}
