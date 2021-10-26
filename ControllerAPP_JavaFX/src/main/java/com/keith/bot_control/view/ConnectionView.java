package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.ConnectionControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConnectionView {

    SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
    ConnectionControl control = BotControlAPP.getConnectionControl();

    @FXML
    private Button connectButton;

    @FXML
    private TextField brokerIP;

    @FXML
    private TextArea logs;

    public ConnectionView(){
        control.setView(this);
    }

    @FXML
    public void initialize(){
        // FXML initialized and populated vars
        control.updateView();
    }

    @FXML
    protected synchronized void onConnectButtonClick() {
        control.buttonClick();
    }

    public Button getConnectButton(){
        return connectButton;
    }

    public TextField getBrokerIP(){
        return brokerIP;
    }

    public void log(String msg){
        logs.appendText(String.format("[%s]\n%s\n",dateFormatter.format(new Date()), msg));
        logs.setScrollTop(Double.MAX_VALUE);
    }
}
