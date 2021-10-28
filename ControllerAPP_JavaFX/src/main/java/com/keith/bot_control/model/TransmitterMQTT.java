package com.keith.bot_control.model;

import com.keith.bot_control.controller.ConnectionControl;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TransmitterMQTT {
    // Topics const
    public final String BROKER_ADDR;
    public static final String USERNAME = "default";
    public static final String PASSWORD = "default";
    // Robot send its UUID on startup once, to this topic:
    public static final String UUID_TOPIC = "T/LightBot/UUID";
    // Robot listen to broadcast from this topic:
    public static final String BROADCAST_TOPIC = "T/LightBot/Broadcast";

    // MQTT related vars
    private final UUID uuid;
    private final MqttClient mqttClient;
    private final MqttConnectOptions connOpts;


    public TransmitterMQTT(UUID uuid, String brokerAddress, ConnectionControl control) throws MqttException {
        BROKER_ADDR = String.format("tcp://%s", brokerAddress);
        System.out.printf("Connecting to message broker at: [%s]...\n", BROKER_ADDR);
        // Initialize vars
        this.uuid = uuid;

        // Initialize MQTT
        mqttClient = new MqttClient(BROKER_ADDR, "BotControl/" + uuid);
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(USERNAME);
        connOpts.setPassword(PASSWORD.toCharArray());
        mqttClient.connect(connOpts);

        System.out.println("Connected");

        // Robot listen to control topic, uuid reports, and broadcast
        String topic = "T/BotControl/" + uuid;
        mqttClient.subscribe(topic, 0);
        mqttClient.subscribe(UUID_TOPIC, 0);
        mqttClient.subscribe(BROADCAST_TOPIC, 0);

        // Setup msg callback
        mqttClient.setCallback(new MqttCallback() {

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
//                System.out.println("\nReceived message:" +
//                        "\n\tTopic:   " + topic +
//                        "\n\tMessage: " + msg +
//                        "\n\tQoS:     " + message.getQos() + "\n");

                // Save message to queue
                control.queueMsg(new BotMessage(topic, msg));
            }

            public void connectionLost(Throwable cause) {
                System.out.println("Lost connection to event broker!" + cause.getMessage());
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }

        });

    }

    // Disconnect mqtt client
    public void disconnect(){
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void reportUUID() throws MqttException {
        MqttMessage newMessage = new MqttMessage(uuid.toString().getBytes(StandardCharsets.UTF_8));
        newMessage.setQos(0);
        mqttClient.publish(UUID_TOPIC, newMessage);
    }


}
