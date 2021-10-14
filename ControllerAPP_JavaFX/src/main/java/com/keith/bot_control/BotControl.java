package com.keith.bot_control;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;

public class BotControl {
    UUID uuid;
    Transmitter transmitter;

    public BotControl() {
        uuid = UUID.randomUUID();
    }

    public boolean initTransmitter(String brokerAddress){
        try {
            transmitter = new Transmitter(uuid, brokerAddress);
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
