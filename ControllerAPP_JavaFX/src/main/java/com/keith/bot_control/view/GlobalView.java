package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.BotControl;
import javafx.fxml.FXML;

public class GlobalView {

    private BotControl control = BotControlAPP.getBotControl();

    @FXML
    public void initialize(){

    }

    @FXML
    public void save(){
        control.save();
    }

    @FXML
    public void load(){
        control.load();
    }

}