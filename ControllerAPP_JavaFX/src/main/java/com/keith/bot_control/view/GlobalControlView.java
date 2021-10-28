package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.GlobalOptionControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GlobalControlView {

    GlobalOptionControl control = BotControlAPP.getBotControl().getGlobalControl();

    @FXML
    private Label connectedBotsCount;

    @FXML
    private Button playButton;

    public GlobalControlView(){

    }

    @FXML
    public void initialize(){
        control.setView(this);
        control.updateView();
    }

    @FXML
    protected void onPlayButtonClick(){
        control.play();
    }

    public Button getPlayButton(){
        return playButton;
    }

}
