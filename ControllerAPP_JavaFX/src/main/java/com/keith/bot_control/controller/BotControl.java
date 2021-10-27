package com.keith.bot_control.controller;

import java.util.UUID;

public class BotControl {
    private UUID uuid;

    // Controllers
    private ConnectionControl connectionControl;
    private DotsCanvasControl dotsCanvasControl;
    private GlobalControl globalControl;

    // States
    private ConnectionControl.State connectionState;
    private GlobalControl.State globalState;

    public BotControl() {
        uuid = UUID.randomUUID();
        // Initialize all controllers
        connectionControl = new ConnectionControl(this);
        dotsCanvasControl = new DotsCanvasControl();
        globalControl = new GlobalControl(this);
        // Initial states
        connectionState = ConnectionControl.State.DISCONNECTED;
        globalState = GlobalControl.State.IDLE;
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

    public GlobalControl getGlobalControl(){
        return globalControl;
    }


    /* State update */

    public ConnectionControl.State getConnectionState(){
        return connectionState;
    }

    public GlobalControl.State getGlobalState(){
        return globalState;
    }

    public void updateConnectionState(ConnectionControl.State state){
        connectionState = state;
        switch (connectionState){
            case CONNECTED -> {
                // Update global state to READY
                updateGlobalState(GlobalControl.State.READY);
            }
            case DISCONNECTED -> {
                updateGlobalState(GlobalControl.State.IDLE);
            }
        }
        connectionControl.updateView();
    }

    public void updateGlobalState(GlobalControl.State state){
        globalState = state;
        globalControl.updateView();
    }

    /* Bot Control Functions */

    public void terminate(){
        connectionControl.resetTransmitter();
    }

}
