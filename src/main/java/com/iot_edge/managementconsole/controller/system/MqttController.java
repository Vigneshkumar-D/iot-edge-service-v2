package com.iot_edge.managementconsole.controller.system;

import com.iot_edge.managementconsole.dto.system.MqttConfigDTO;
import com.iot_edge.managementconsole.service.system.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mqtt")
public class MqttController {


    private final MqttService mqttService;

    public MqttController(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    @PostMapping("/connect")
    public ResponseEntity<String> connectToMqtt(@RequestBody MqttConfigDTO request) {
        String response = mqttService.connectToMqtt(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disconnect")
    public String subscribeToTopic() {
        return mqttService.disconnectFromMqtt();
    }

    @PostMapping("/subscribe")
    public String disconnectToTopic(@RequestParam("topic") String topic) {
        return mqttService.subscribeToTopic(topic);
    }
}
