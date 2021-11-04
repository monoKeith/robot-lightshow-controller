package com.keith.bot_control.controller;

import com.keith.bot_control.view.GlobalControlView;
import javafx.application.Platform;

public class GlobalOptionControl {

    public enum State{
        PLAYING,
        PREVIEW,
        READY,
        IDLE
    }

    private BotControl botControl;
    private GlobalControlView view;

    public GlobalOptionControl(BotControl botControl){
        this.botControl = botControl;
        view = null;
    }

    public void setView(GlobalControlView view){
        this.view = view;
    }

    public void play(){
        if (botControl.getGlobalState() != State.READY) return;
        botControl.updateGlobalState(State.PLAYING);
        log("Play!");
        // TODO Play animation

        botControl.updateGlobalState(State.READY);
    }

    public void preview(){
        if (botControl.getGlobalState() != State.READY) return;
        botControl.updateGlobalState(State.PREVIEW);
        log("Preview frame");
        botControl.publishTargets();
        botControl.updateGlobalState(State.READY);
    }

    public void refreshView(){
        if (view == null) return;
        Platform.runLater(() -> {
            // Button state
            view.updateButtonState(botControl.getGlobalState());
            // Connected Bot counts
            view.setConnectedBotsCount(botControl.getConnectedBots().size());
            // Current Frame name
            view.setCurrentFrameName(botControl.getCurrentFrame().getName());
        });
    }

    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName() , msg);
    }

}
