package com.iot_edge.managementconsole.engine;

import com.iot_edge.managementconsole.entity.system.IoTGateway;
import com.iot_edge.managementconsole.service.system.MqttMessageListener;
import com.iot_edge.managementconsole.service.system.MqttService;
import com.iot_edge.managementconsole.service.system.ScriptService;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AssetEngine {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);
    private MqttClient mqttClient;

    @Autowired
    private MqttMessageListener mqttMessageListener;

    @Autowired
    private ScriptService service;

    public String connectToMqtt(IoTGateway ioTGateway) {
        if (ioTGateway.getBrokerUrl() == null || ioTGateway.getBrokerUrl().isEmpty()) {
            return "Broker URL is required!";
        }
        if (ioTGateway.getClientId() == null || ioTGateway.getClientId().isEmpty()) {
            return "Client ID is required!";
        }
        try {
            mqttClient = new MqttClient(ioTGateway.getBrokerUrl(), ioTGateway.getClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);

            options.setUserName(ioTGateway.getUserName());
            options.setPassword(ioTGateway.getPassword().toCharArray());
            mqttClient.connect(options);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String receivedMessage = new String(message.getPayload());
                    logger.info("Received message on topic {}: {}", topic, receivedMessage);
                    mqttMessageListener.handleMessage(topic, receivedMessage);
                    service.executeScript(receivedMessage);
                }

                @Override
                public void connectionLost(Throwable cause) {
                    logger.error("Connection lost: {}", cause.getMessage(), cause);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    logger.info("Message delivery complete: {}", token.getMessageId());
                }
            });
            logger.info("Connected successfully to {}", ioTGateway.getBrokerUrl());
            return "Connected successfully to " + ioTGateway.getBrokerUrl();
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
            return "Subscribed successfully to topic: " + topic;
        } catch (MqttException e) {
            logger.error("Failed to subscribe: " + e.getMessage(), e);
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

}
