package com.iot_edge.managementconsole.controller.system;

import com.iot_edge.managementconsole.dto.system.IoTGatewayDto;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.service.system.IoTGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("iot-gateway")
public class IoTGatewayController {

    @Autowired
    private IoTGatewayService ioTGatewayService;

    @PostMapping
    public ResponseEntity<ResponseModel<?>> add(@RequestBody IoTGatewayDto ioTGatewayDto) {
        return ioTGatewayService.add(ioTGatewayDto);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<?>> list() {
        return ioTGatewayService.list();
    }

    @PutMapping("/{iotGatewayId}")
    public ResponseEntity<ResponseModel<?>> update(@PathVariable("iotGatewayId") Integer iotGatewayId, @RequestBody IoTGatewayDto ioTGatewayDto){
        return ioTGatewayService.update(iotGatewayId, ioTGatewayDto);
    }

    @DeleteMapping("/{iotGatewayId}")
    public ResponseEntity<ResponseModel<?>> delete(@PathVariable Integer iotGatewayId) {
        return ioTGatewayService.delete(iotGatewayId);
    }
}
