package com.keith.bot_control;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.UUID;

public class Transmitter {
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

    // Queue to save received messages
    private LinkedList<String> receiveQueue;

    public Transmitter(UUID uuid, String brokerAddress) throws MqttException {
        BROKER_ADDR = String.format("tcp://%s", brokerAddress);
        System.out.printf("Connecting to message broker at: [%s]...\n", BROKER_ADDR);
        // Initialize vars
        this.uuid = uuid;
        receiveQueue = new LinkedList<String>();

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
                System.out.println("\nReceived message:" +
                        "\n\tTopic:   " + topic +
                        "\n\tMessage: " +  msg +
                        "\n\tQoS:     " + message.getQos() + "\n");

                // Save message to queue
                queueMsg(msg);
            }

            public void connectionLost(Throwable cause) {
                System.out.println("Connection to Solace broker lost!" + cause.getMessage());
                System.exit(1);
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }

        });

        // Report UUID once during startup
        reportUUID();
    }

    private synchronized void queueMsg(String newMsg){
        receiveQueue.add(newMsg);
        notifyAll();
    }

    // Get the newest message, stuck here if no new message
    public synchronized String waitForMsg() throws InterruptedException {
        while(receiveQueue.isEmpty()){
            wait();
        }
        String newMsg = receiveQueue.pop();
        notifyAll();
        return newMsg;
    }

    public void reportUUID() throws MqttException {
        MqttMessage newMessage = new MqttMessage(uuid.toString().getBytes(StandardCharsets.UTF_8));
        newMessage.setQos(0);
        mqttClient.publish(UUID_TOPIC, newMessage);
    }


}
