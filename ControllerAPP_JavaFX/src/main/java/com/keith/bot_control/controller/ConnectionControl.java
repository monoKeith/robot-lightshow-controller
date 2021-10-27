package com.keith.bot_control.controller;

import com.keith.bot_control.model.Transmitter;
import com.keith.bot_control.view.ConnectionView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;

public class ConnectionControl {

    public enum State {
        CONNECTED,
        CONNECTING,
        DISCONNECTED;
    }

    BotControl botControl;
    Transmitter transmitter;
    UUID uuid;
    String brokerAddress;
    ConnectionView view;

    public ConnectionControl(BotControl botControl){
        this.botControl = botControl;
        this.uuid = botControl.getUuid();
        this.brokerAddress = "localhost";
        this.view = null;
    }

    public void setView(ConnectionView view){
        this.view = view;
    }

    public void buttonClick(){
        switch (botControl.getConnectionState()){
            case CONNECTED -> {
                // To Disconnect
                resetTransmitter();
                updateView();
            }
            case DISCONNECTED -> {
                // To Connect
                botControl.updateConnectionState(State.CONNECTING);
                brokerAddress = view.getBrokerIP().getText();
                initTransmitter(brokerAddress);
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
            botControl.updateConnectionState(State.CONNECTED);
        } catch (MqttException e) {
            e.printStackTrace();
            view.log("Failed to establish connection");
            botControl.updateConnectionState(State.DISCONNECTED);
        }
    }

    public void updateView(){
        if (view == null) return;
        Button connectButton = view.getConnectButton();
        TextField brokerIP = view.getBrokerIP();
        switch (botControl.getConnectionState()) {
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
        view.log(String.format("current status: %s\n", botControl.getConnectionState().name()));

    }

    public void resetTransmitter(){
        if (transmitter != null){
            transmitter.disconnect();
            transmitter = null;
        }
        botControl.updateConnectionState(State.DISCONNECTED);
    }

}
