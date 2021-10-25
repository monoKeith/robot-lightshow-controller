package com.keith.bot_control.controller;

import java.util.UUID;

public class BotControl {
    UUID uuid;
    private ConnectionControl connectionControl;

    public BotControl() {
        uuid = UUID.randomUUID();
        connectionControl = new ConnectionControl(uuid);
    }

    public ConnectionControl getConnectionControl(){
        return connectionControl;
    }

    public void terminate(){
        connectionControl.resetTransmitter();
    }

}
