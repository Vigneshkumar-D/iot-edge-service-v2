package com.iot_edge.managementconsole.controller.system;

import com.iot_edge.managementconsole.dto.system.FirmDto;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.service.system.FirmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/firm")
public class FirmController {

    @Autowired
    private FirmService firmService;

    @PostMapping
    public ResponseEntity<ResponseModel<?>> add(@RequestBody FirmDto firmDto) {
        return firmService.add(firmDto);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<?>> list() {
        return firmService.list();
    }

    @PutMapping("/{firmId}")
    public ResponseEntity<ResponseModel<?>> update(@PathVariable("firmId") Integer firmId, @RequestBody FirmDto firmDto){
        return firmService.update(firmId, firmDto);
    }

    @DeleteMapping("/{firmId}")
    public ResponseEntity<ResponseModel<?>> delete(@PathVariable Integer firmId) {
        return firmService.delete(firmId);
    }

}
