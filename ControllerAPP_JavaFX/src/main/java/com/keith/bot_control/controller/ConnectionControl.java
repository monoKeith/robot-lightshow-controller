package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotMessage;
import com.keith.bot_control.model.TransmitterMQTT;
import com.keith.bot_control.view.ConnectionView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.LinkedList;
import java.util.UUID;

public class ConnectionControl {

    public enum State {
        CONNECTED,
        CONNECTING,
        DISCONNECTED;
    }

    BotControl botControl;
    TransmitterMQTT transmitter;
    UUID uuid;
    String brokerAddress;
    ConnectionView view;

    Boolean terminateFlag;

    // Queue to save received messages
    private LinkedList<BotMessage> receiveQueue;

    public ConnectionControl(BotControl botControl){
        this.botControl = botControl;
        this.uuid = botControl.getUuid();
        this.brokerAddress = "localhost";
        this.view = null;
        this.receiveQueue = new LinkedList<>();
        this.terminateFlag = false;
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

    public synchronized void initTransmitter(String brokerAddress){
        if (transmitter != null) return;
        try {
            transmitter = new TransmitterMQTT(uuid, brokerAddress, this);
            botControl.updateConnectionState(State.CONNECTED);
            // waitForMsg() might be waiting for transmitter to be initialized
            notifyAll();
        } catch (MqttException e) {
            e.printStackTrace();
            view.log("Failed to establish connection");
            botControl.updateConnectionState(State.DISCONNECTED);
        }
    }

    public void updateView(){
        if (view == null) return;
        Platform.runLater(() -> {
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
        });
    }

    public synchronized void resetTransmitter(){
        if (transmitter != null){
            transmitter.disconnect();
            transmitter = null;
        }
        botControl.updateConnectionState(State.DISCONNECTED);
    }



    /* Receive message */

    public synchronized void terminateMsgQueue(){
        terminateFlag = true;
        notifyAll();
    }

    public synchronized void queueMsg(BotMessage newMsg){
        receiveQueue.add(newMsg);
        notifyAll();
    }

    // Synchronized: Get message from transmitter
    // Stuck until new message comes
    public synchronized BotMessage waitForMsg(){
        // Wait if transmitter is not initialized
        while (transmitter == null || receiveQueue.isEmpty()) {
            try {
                wait();
                if (terminateFlag) return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        notifyAll();
        return receiveQueue.pop();
    }

}
