package com.keith.bot_control.controller;

import com.keith.bot_control.model.Transmitter;
import com.keith.bot_control.view.ConnectionView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
        TextField brokerIP = view.getBrokerIP();
        switch (state) {
            case CONNECTED -> {
                connectButton.setText("Disconnect");
                connectButton.setDisable(false);
                brokerIP.setDisable(true);
            }
            case CONNECTING -> {
                connectButton.setText("Connecting");
                connectButton.setDisable(true);
                brokerIP.setDisable(true);
                view.log(String.format("connecting to message broker at: %s\n", brokerAddress));
            }
            case DISCONNECTED -> {
                connectButton.setText("Connect");
                connectButton.setDisable(false);
                brokerIP.setDisable(false);
            }
        }
        view.log(String.format("current status: %s\n", state.name()));

    }

    public void resetTransmitter(){
        if (transmitter != null){
            transmitter.disconnect();
            transmitter = null;
        }
        state = ConnectionState.DISCONNECTED;
    }

}