package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotMessage;
import com.keith.bot_control.model.TransmitterMQTT;
import com.keith.bot_control.view.ConnectionView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.LinkedList;

public class ConnectionControl {

    public enum State {
        CONNECTED,
        CONNECTING,
        DISCONNECTED
    }

    private final BotControl botControl;
    private TransmitterMQTT transmitter;
    private String brokerAddress;
    private ConnectionView view;

    // Once set to true, goes in termination state and connectionControl can't be recovered.
    private Boolean terminateFlag;

    // Queue to store messages to be published
    private final LinkedList<BotMessage> publishQueue;

    // Queue to save received messages
    private final LinkedList<BotMessage> receiveQueue;


    public ConnectionControl(BotControl botControl){
        this.botControl = botControl;
        this.brokerAddress = "localhost";
        this.view = null;
        this.publishQueue = new LinkedList<>();
        this.receiveQueue = new LinkedList<>();
        this.terminateFlag = false;

        initPublishProcessor();
    }

    public void setView(ConnectionView view){
        this.view = view;
    }

    public void buttonClick(){
        switch (botControl.getConnectionState()){
            case CONNECTED -> {
                // To Disconnect
                resetTransmitter();
            }
            case DISCONNECTED -> {
                // To Connect
                brokerAddress = view.getBrokerIP().getText();
                initTransmitter(brokerAddress);
            }
        }
    }

    public synchronized void initTransmitter(String brokerAddress){
        if (transmitter != null) return;
        botControl.updateConnectionState(State.CONNECTING);
        try {
            transmitter = new TransmitterMQTT(botControl.getUuid(), brokerAddress, this);
            botControl.updateConnectionState(State.CONNECTED);
            // waitForMsg() might be waiting for transmitter to be initialized
            notifyAll();
        } catch (MqttException e) {
            e.printStackTrace();
            view.log("Failed to establish connection");
            botControl.updateConnectionState(State.DISCONNECTED);
        }
    }

    public void refreshView(){
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


    /* Send message */

    private void initPublishProcessor(){
        new Thread(this::publishProcessor).start();
    }

    private void publishProcessor(){
        while(true){
            synchronized (publishQueue) {
                // Wait for message to publish
                while (transmitter == null || publishQueue.isEmpty()) {
                    try {
                        publishQueue.wait();
                        if (terminateFlag) return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                // Attempt to publish
                try {
                    log("publishing");
                    transmitter.publish(publishQueue.removeFirst());
                    log("published");
                } catch (MqttException e) {
                    log("failed to publish message, bad connection?");
                    e.printStackTrace();
                }
            }
        }
    }

    public void publishMessage(BotMessage message){
        synchronized (publishQueue) {
            publishQueue.add(message);
            log("publish message queue: " + message);
            publishQueue.notifyAll();
        }
    }

    /* Receive message */

    public synchronized void terminateMsgQueue(){
        terminateFlag = true;
        synchronized (publishQueue) {
            publishQueue.notifyAll();
        }
        synchronized (receiveQueue) {
            receiveQueue.notifyAll();
        }
    }

    public void queueMsg(BotMessage newMsg){
        synchronized (receiveQueue){
            receiveQueue.add(newMsg);
            receiveQueue.notifyAll();
        }
    }

    // Stuck until new message comes
    public BotMessage waitForMsg(){
        synchronized (receiveQueue) {
            while (receiveQueue.isEmpty()) {
                try {
                    receiveQueue.wait();
                    if (terminateFlag) return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            BotMessage msg = receiveQueue.pop();
            receiveQueue.notifyAll();
            return msg;
        }
    }


    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName(), msg);
    }

}
