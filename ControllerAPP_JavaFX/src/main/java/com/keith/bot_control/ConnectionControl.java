package com.keith.bot_control;

import javafx.scene.control.Button;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;

public class ConnectionControl {

    public enum ConnectionState{
        CONNECTED,
        CONNECTING,
        DISCONNECTED;
    }

    Transmitter transmitter;
    ConnectionState state;
    UUID uuid;
    String brokerAddress;
    ConnectionView view;

    public ConnectionControl(UUID uuid){
        this.uuid = uuid;
        this.state = ConnectionState.DISCONNECTED;
        this.brokerAddress = "localhost";
        this.view = null;
    }

    public void setView(ConnectionView view){
        this.view = view;
    }

    public void buttonClick(){
        switch (state){
            case CONNECTED -> {
                // To Disconnect
                resetTransmitter();
                updateView();
            }
            case DISCONNECTED -> {
                // To Connect
                state = ConnectionState.CONNECTING;
                brokerAddress = view.getBrokerIP().getText();
                updateView();
                initTransmitter(brokerAddress);
                updateView();
            }
        }
    }

    // Used by view class to set broker address
    public void setBrokerAddress(String newAddress){
        brokerAddress = newAddress;
    }

    public void initTransmitter(String brokerAddress){
        try {
            transmitter = new Transmitter(uuid, brokerAddress);
            state = ConnectionState.CONNECTED;
        } catch (MqttException e) {
            e.printStackTrace();
            state = ConnectionState.DISCONNECTED;
        }
    }

    public void updateView(){
        if (view == null) return;
        Button connectButton = view.getConnectButton();
        switch (state) {
            case CONNECTED -> {
                connectButton.setText("Disconnect");
                connectButton.setDisable(false);
            }
            case CONNECTING -> {
                connectButton.setText("Connecting");
                connectButton.setDisable(true);
            }
            case DISCONNECTED -> {
                connectButton.setText("Connect");
                connectButton.setDisable(false);
            }
        }
        view.log(String.format("State update: %s\n", state.name()));
    }

    public void resetTransmitter(){
        if (transmitter != null){
            transmitter.disconnect();
            transmitter = null;
        }
        state = ConnectionState.DISCONNECTED;
    }

}
