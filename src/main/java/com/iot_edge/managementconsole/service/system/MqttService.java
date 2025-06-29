package com.iot_edge.managementconsole.service.system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.energy.dto.EnergyConsumptionDto;
import com.iot_edge.energy.service.EnergyConsumptionService;
import com.iot_edge.managementconsole.constants.AssetCategory;
import com.iot_edge.managementconsole.dto.system.MqttConfigDTO;
import com.iot_edge.managementconsole.entity.system.Asset;
import com.iot_edge.managementconsole.repository.system.AssetRepository;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    private MqttClient mqttClient;

    private final MqttMessageListener mqttMessageListener;

    private final ScriptService service;

    private final AssetRepository assetRepository;

    private final EnergyConsumptionService energyConsumptionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MqttService( MqttMessageListener mqttMessageListener, ScriptService service, AssetRepository assetRepository, EnergyConsumptionService energyConsumptionService) {
        this.mqttMessageListener = mqttMessageListener;
        this.service = service;
        this.assetRepository = assetRepository;
        this.energyConsumptionService = energyConsumptionService;
    }


    public String connectToMqtt(MqttConfigDTO request) {
        if (request.getBrokerUrl() == null || request.getBrokerUrl().isEmpty()) {
            return "Broker URL is required!";
        }
//        if (request.getClientId() == null || request.getClientId().isEmpty()) {
//            return "Client ID is required!";
//        }
        try {
            mqttClient = new MqttClient(request.getBrokerUrl(), request.getClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setUserName(request.getUsername());
            options.setPassword(request.getPassword().toCharArray());
            mqttClient.connect(options);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String receivedMessage = new String(message.getPayload());
                    logger.info("Received message on topic {}: {}", topic, receivedMessage);
                    mqttMessageListener.handleMessage(topic, receivedMessage);
                    saveReceivedData(topic, receivedMessage);
                    System.out.println("Logggggg");
                    service.executeScript(receivedMessage);
                }
                @Override
                public void connectionLost(Throwable cause) {
//                    try {
//                        mqttClient.reconnect(); // Attempt to reconnect
//                    } catch (MqttException e) {
//                        e.printStackTrace();
//                    }
                    logger.error("Connection lost: {}", cause.getMessage(), cause);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    logger.info("Message delivery complete: {}", token.getMessageId());
                }
            });
            logger.info("Connected successfully to {}", request.getBrokerUrl());
            return "Connected successfully to " + request.getBrokerUrl();
        } catch (MqttException e) {
            logger.error("Failed to connect: " + e.getMessage(), e);
            return "Failed to connect: " + e.getMessage();
        }
    }

    public String subscribeToTopic(String topic) {
        try {
            if (mqttClient == null || !mqttClient.isConnected()) {
                return "MQTT Client is not connected!";
            }
            mqttClient.subscribe(topic, 1);
            logger.info("Subscribed successfully to topic: {}", topic);
            return "Subscribed successfully to topic: " + topic;
        } catch (MqttException e) {
            logger.error("Failed to subscribe: {}", e.getMessage(), e);
            return "Failed to subscribe: " + e.getMessage();
        }
    }

    public String disconnectFromMqtt() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.info("Disconnected successfully from MQTT broker.");
                return "Disconnected successfully from MQTT broker.";
            } else {
                logger.warn("MQTT Client is not connected!");
                return "MQTT Client is not connected!";
            }
        } catch (MqttException e) {
            logger.error("Failed to disconnect: " + e.getMessage(), e);
            return "Failed to disconnect: " + e.getMessage();
        }
    }

    private void saveReceivedData(String topic, String payload) throws JsonProcessingException {
        Asset asset = assetRepository.findBySubTopicName(topic);

        if (asset != null) {
            if(asset.getAssetCategory().equals(AssetCategory.ENERGY)){
                JsonNode jsonNode = objectMapper.readTree(payload);

                Set<String> allowedKeys = Set.of("meterReading", "current", "voltage", "frequency", "powerFactor");

                Map<String, Double> readings = new HashMap<>();

                jsonNode.fields().forEachRemaining(entry -> {
                    if (allowedKeys.contains(entry.getKey()) && entry.getValue().isNumber()) {
                        readings.put(entry.getKey(), entry.getValue().asDouble());
                    }
                });

                double meterReading = readings.get("meterReading");
                double current = readings.get("current");
                double voltage = readings.get("voltage");
                double frequency = readings.get("frequency");
                double powerFactor = readings.get("powerFactor");
                Instant timestamp = Instant.parse(jsonNode.get("timestamp").asText());
                EnergyConsumptionDto energyConsumptionDto = new EnergyConsumptionDto(asset, meterReading, current, voltage, frequency, powerFactor, timestamp);
                energyConsumptionService.calculateEnergyConsumption(energyConsumptionDto);
            }
        } else {
            System.err.println("No asset mapped to the topic: " + topic);
        }
    }

}
