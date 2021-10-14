package com.keith.bot_control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    protected void onConnectButtonClick() {
        if (botControl.initTransmitter(brokerIP.getText())){
            connectButton.setDisable(true);
            brokerIP.setDisable(true);
            log("Connected to event broker\n");
        } else {
            log("Failed to connect to event broker\n");
        }

    }

    private void log(String msg){
        logs.appendText(String.format("[%s]\n%s\n",dateFormatter.format(new Date()), msg));
    }
}
