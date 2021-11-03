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


    /* Refresh */

    public void refreshView(){
        Platform.runLater(() -> {
            // Clear current frame?

            // Add all frames
            for (BotFrame frame: control.getFrames()){
                view.addFrame(frame);
            }

        });
    }


    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName() , msg);
    }

}
