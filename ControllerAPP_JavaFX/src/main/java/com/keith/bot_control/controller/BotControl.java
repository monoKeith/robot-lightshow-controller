package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotMessage;
import com.keith.bot_control.model.TransmitterMQTT;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BotControl {
    private UUID uuid;

    // Controllers
    private ConnectionControl connectionControl;
    private DotsCanvasControl dotsCanvasControl;
    private GlobalOptionControl globalControl;

    // States
    private ConnectionControl.State connectionState;
    private GlobalOptionControl.State globalState;

    Thread msgProcessor;
    Boolean msgProcessorStopSignal;
    // Set of UUIDs of connected bots
    Set<UUID> connectedBots;

    public BotControl() {
        uuid = UUID.randomUUID();
        // Initialize all controllers
        connectionControl = new ConnectionControl(this);
        dotsCanvasControl = new DotsCanvasControl();
        globalControl = new GlobalOptionControl(this);
        // Initial states
        connectionState = ConnectionControl.State.DISCONNECTED;
        globalState = GlobalOptionControl.State.IDLE;
        // Init vars
        connectedBots = new HashSet<>();
        // Init message processor
        initMsgProcessor();
    }

    /* Getters */

    public UUID getUuid(){
        return uuid;
    }

    public ConnectionControl getConnectionControl(){
        return connectionControl;
    }

    public DotsCanvasControl getDotsControl(){
        return dotsCanvasControl;
    }

    public GlobalOptionControl getGlobalControl(){
        return globalControl;
    }

    public Set<UUID> getConnectedBots(){
        return connectedBots;
    }


    /* State update */

    public ConnectionControl.State getConnectionState(){
        return connectionState;
    }

    public GlobalOptionControl.State getGlobalState(){
        return globalState;
    }

    public void updateConnectionState(ConnectionControl.State state){
        if (connectionState == state) return;
        connectionState = state;
        switch (connectionState){
            case CONNECTED -> {
                // Update global state to READY
                updateGlobalState(GlobalOptionControl.State.READY);
            }
            case DISCONNECTED -> {
                // Update global state to IDLE
                connectedBots = new HashSet<>();
                updateGlobalState(GlobalOptionControl.State.IDLE);
            }
        }
        connectionControl.updateView();
    }

    public void updateGlobalState(GlobalOptionControl.State state){
        if (globalState == state) return;
        globalState = state;
        globalControl.updateView();
    }

    /* Message processor - Receives message from transmitter and process */

    // Spawn a new thread to receive message
    private void initMsgProcessor(){
        // Do nothing if already exist
        if (msgProcessor != null) return;
        // New thread
        msgProcessorStopSignal = false;
        msgProcessor = new Thread(this::receiveMessage);
        msgProcessor.start();
    }

    private void terminateMsgProcessor(){
        if (msgProcessor == null) return;
        msgProcessorStopSignal = true;
        connectionControl.terminateMsgQueue();
        try {
            msgProcessor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage(){
        while(!msgProcessorStopSignal){
            BotMessage msg = connectionControl.waitForMsg();
            if (msg == null) continue;
            processMessage(msg);
        }
        System.out.println("message processor thread terminated");
    }

    private void processMessage(BotMessage msg){
        System.out.println("processing message: " + msg);
        switch (msg.getTopic()){
            case TransmitterMQTT.UUID_TOPIC -> {
                connectedBots.add(UUID.fromString(msg.getMessage()));
                globalControl.updateView();
            }
        }
    }

    /* Bot Control Functions */

    public void terminate(){
        connectionControl.resetTransmitter();
        terminateMsgProcessor();
    }

}
