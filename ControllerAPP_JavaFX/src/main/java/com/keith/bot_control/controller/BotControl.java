package com.keith.bot_control.controller;

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


    /* State update */

    public ConnectionControl.State getConnectionState(){
        return connectionState;
    }

    public GlobalOptionControl.State getGlobalState(){
        return globalState;
    }

    public void updateConnectionState(ConnectionControl.State state){
        connectionState = state;
        switch (connectionState){
            case CONNECTED -> {
                // Update global state to READY
                updateGlobalState(GlobalOptionControl.State.READY);
            }
            case DISCONNECTED -> {
                // Update global state to IDLE
                updateGlobalState(GlobalOptionControl.State.IDLE);
            }
        }
        connectionControl.updateView();
    }

    public void updateGlobalState(GlobalOptionControl.State state){
        globalState = state;
        globalControl.updateView();
    }

    /* Message processor - Receives message from transmitter and process */

    // Spawn a new thread to receive message
    public void initMsgProcessor(){
        // Terminate if already exist
        if (msgProcessor != null) {
            msgProcessorStopSignal = true;
            try {
                msgProcessor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        // New thread
        msgProcessorStopSignal = false;
        msgProcessor = new Thread(this::processMsg);
        msgProcessor.start();
    }

    public void processMsg(){
        while(!msgProcessorStopSignal){
            String msg = connectionControl.waitForMsg();
            System.out.println("BotControl processing msg: " + msg);
        }
    }

    /* Bot Control Functions */

    public void terminate(){
        connectionControl.resetTransmitter();
    }

}
