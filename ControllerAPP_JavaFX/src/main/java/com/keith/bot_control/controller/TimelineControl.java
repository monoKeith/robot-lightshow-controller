package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotFrame;
import com.keith.bot_control.view.TimelineView;
import javafx.application.Platform;

public class TimelineControl {

    private  BotControl control;
    private TimelineView view;

    public TimelineControl(BotControl botControl){
        this.control = botControl;
        view = null;
    }

    public void setView(TimelineView view){
        this.view = view;
    }

    /* Selection */

    public void selectFrame(BotFrame frame){
        if (frame == control.getCurrentFrame()) return;
        log("select: " + frame);
        control.updateCurrentFrame(frame);
        refreshView();
    }


    /* Refresh */

    public void initView(){
        // Clear current frame?

        // Add all frames
        for (BotFrame frame: control.getFrames()){
            view.addFrame(frame);
        }
    }

    public void refreshView(){
        Platform.runLater(() -> {

            view.refreshFrames();

        });
    }

    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName() , msg);
    }

}
