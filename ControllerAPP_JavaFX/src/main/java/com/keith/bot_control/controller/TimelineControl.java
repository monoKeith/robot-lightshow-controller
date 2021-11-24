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
        BotFrame previousFrame = control.getCurrentFrame();
        if (frame == previousFrame) return;

        log("select: " + frame);
        control.updateCurrentFrame(frame);
        refreshFrame(previousFrame);
        refreshCurrentFrame();
    }


    /* Refresh */

    public void initView(){
        // Clear current frame?

        // Add all frames
        for (BotFrame frame: control.getFrames()){
            view.addFrame(frame);
        }
    }

    public void refreshEntireView(){
        Platform.runLater(() -> {
            view.refreshAllFrames();
        });
    }

    public void refreshSelection(){
        Platform.runLater(() -> {
            view.refreshSelection();
        });
    }

    // Refresh view of a specific frame on the Timeline
    // Used when properties of a frame is updated
    public void refreshFrame(BotFrame frame){
        Platform.runLater(() -> view.refreshFrame(frame));
    }

    public void refreshCurrentFrame(){
        BotFrame selectedFrame = control.getCurrentFrame();
        Platform.runLater(() -> view.refreshFrame(selectedFrame));
    }

    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName() , msg);
    }

}
