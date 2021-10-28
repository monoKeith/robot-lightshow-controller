package com.keith.bot_control.controller;

import com.keith.bot_control.view.GlobalControlView;
import javafx.application.Platform;

public class GlobalOptionControl {

    enum State{
        PLAYING,
        READY,
        IDLE;
    }

    BotControl botControl;
    GlobalControlView view;

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
        System.out.println("Play!");

    }

    public void updateView(){
        if (view == null) return;
        Platform.runLater(() -> {
            switch (botControl.getGlobalState()){
                case PLAYING -> {
                    view.getPlayButton().setText("Playing");
                    view.getPlayButton().setDisable(true);
                }
                case READY -> {
                    view.getPlayButton().setText("Play");
                    view.getPlayButton().setDisable(false);
                }
                case IDLE -> {
                    view.getPlayButton().setText("Play");
                    view.getPlayButton().setDisable(true);
                }
            }
            // Connected Bot counts
            view.setConnectedBotsCount(botControl.getConnectedBots().size());
        });
    }

}
