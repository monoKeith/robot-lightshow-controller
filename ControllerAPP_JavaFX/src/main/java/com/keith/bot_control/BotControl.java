package com.keith.bot_control;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;

public class BotControl {
    UUID uuid;
    Transmitter transmitter;
    Boolean connected;

    public BotControl() {
        uuid = UUID.randomUUID();
        connected = false;
    }

    public boolean initTransmitter(String brokerAddress){
        if (connected) return true;
        try {
            transmitter = new Transmitter(uuid, brokerAddress);
            connected = true;
        } catch (MqttException e) {
            e.printStackTrace();
            connected = false;
        }
        return connected;
    }

    public void resetTransmitter(){
        connected = false;
        transmitter.disconnect();
        transmitter = null;
    }

}
