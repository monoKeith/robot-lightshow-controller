package com.keith.bot_control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConnectionView {

    SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
    BotControl botControl = BotControlAPP.getBotControl();

    @FXML
    private Button connectButton;

    @FXML
    private TextField brokerIP;

    @FXML
    private TextArea logs;

    @FXML
    protected synchronized void onConnectButtonClick() {
        connectButton.setDisable(true);
        if (botControl.connected) {
            // Need to Disconnect
            botControl.resetTransmitter();
            connectButton.setText("Connect");
            log("disconnected from event broker\n");
        } else {
            // Need to Connect
            connectButton.setText("Connecting...");
            if (botControl.initTransmitter(brokerIP.getText())){
                connectButton.setText("Disconnect");
                log("connected to event broker\n");
            } else {
                log("failed to connect to event broker\n");
            }
        }
        connectButton.setDisable(false);
    }

    private void log(String msg){
        logs.appendText(String.format("[%s]\n%s\n",dateFormatter.format(new Date()), msg));
    }
}
