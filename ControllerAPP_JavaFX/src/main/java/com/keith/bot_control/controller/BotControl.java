package com.keith.bot_control.controller;

import java.util.UUID;

public class BotControl {
    UUID uuid;
    private ConnectionControl connectionControl;
    private DotsControl dotsControl;

    public BotControl() {
        uuid = UUID.randomUUID();
        // Initialize all controllers
        connectionControl = new ConnectionControl(uuid);
        dotsControl = new DotsControl();
    }

    public ConnectionControl getConnectionControl(){
        return connectionControl;
    }

    public DotsControl getDotsControl(){
        return dotsControl;
    }

    public void terminate(){
        connectionControl.resetTransmitter();
    }

}
