package com.iot_edge.managementconsole.service.system;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MqttMessageListener {

    private static final ConcurrentHashMap<String, Boolean> assetStatusMap = new ConcurrentHashMap<>();


    private final SimpMessagingTemplate messagingTemplate;

    public MqttMessageListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(@Header("mqtt_receivedTopic") String topic, @Payload String payload) {
        System.out.println("Received MQTT Message: " + payload + " from topic: " + topic);
        String assetId = extractAssetId(topic);
        boolean isConnected = payload.equalsIgnoreCase("CONNECTED");
        assetStatusMap.put(assetId, isConnected);
        HashMap<String, String> data = new HashMap<>();
        data.put(topic, payload);
        messagingTemplate.convertAndSend("/topic/"+topic, data);
    }

    private String extractAssetId(String topic) {
        String[] parts = topic.split("/");
        return parts.length > 1 ? parts[1] : "unknown";
    }
}
