package com.iot_edge.shift.controller;


import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.shift.dto.ShiftDto;
import com.iot_edge.shift.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping
    public ResponseEntity<ResponseModel<?>> list() {
        return shiftService.list();
    }

    @PostMapping
    public ResponseEntity<ResponseModel<?>> createRole(@RequestBody ShiftDto shiftDto) {
        return shiftService.add(shiftDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> updateRole(@PathVariable(name = "id") Long shiftId, @RequestBody ShiftDto shiftDto) {
        return shiftService.update(shiftId, shiftDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<?>> deleteRole(@PathVariable(name = "id") Long shiftId) {
        return shiftService.delete(shiftId);
    }

}
