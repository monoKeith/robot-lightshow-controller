package com.keith.bot_control.view;

import com.keith.bot_control.model.BotFrame;
import javafx.fxml.FXML;

public class FrameView {

    TimelineView controller;
    BotFrame frame;

    @FXML
    protected void initialize(){
    }

    @FXML
    protected void click(){
        System.out.println("oh a click!");
        controller.selectFrame(frame);
    }

    public void setController(TimelineView controller){
        this.controller = controller;
    }

    public void setFrame(BotFrame frame){
        this.frame = frame;
    }
}
