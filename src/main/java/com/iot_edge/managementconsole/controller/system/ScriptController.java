package com.iot_edge.managementconsole.controller.system;

import com.iot_edge.managementconsole.service.system.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/script")
public class ScriptController {

    @Autowired
    private ScriptService service;

    @PostMapping("/execute")
    public Object executeScript(@RequestBody Map<String, Object> payload) {
        String script = (String) payload.get("script");
        Map<String, Object> mqttData = (Map<String, Object>) payload.get("mqttData");
        return service.executeScript(script);
    }
}
