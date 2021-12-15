package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.GlobalOptionControl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GlobalControlView {

    GlobalOptionControl control = BotControlAPP.getBotControl().getGlobalControl();

    @FXML
    protected Label connectedBotsCount;

    @FXML
    protected Label currentFrameName;

    @FXML
    protected Button abortButton, previewButton, playButton, refreshButton;

    public GlobalControlView(){

    }

    @FXML
    public void initialize(){
        control.setView(this);
        control.refreshView();
    }

    @FXML
    protected void onPlayButtonClick(){
        control.play();
    }

    @FXML
    protected void onPreviewButtonClick(){
        control.preview();
    }

    @FXML
    public void refreshConnections(){
        control.refreshConnections();
    }

    @FXML
    public void abort(){
        control.abort();
    }

    public void setConnectedBotsCount(int count){
        connectedBotsCount.setText("" + count);
    }

    public void setCurrentFrameName(String name){
        currentFrameName.setText(name);
    }

    /* State update */

    public void updateButtonState(GlobalOptionControl.State globalState){
        switch (globalState){
            case PLAYING -> {
                playButton.setText("Playing");
                playButton.setDisable(true);
                previewButton.setText("Preview");
                previewButton.setDisable(true);
                refreshButton.setDisable(true);
                abortButton.setDisable(false);
            }
            case PREVIEW -> {
                playButton.setText("Play");
                playButton.setDisable(true);
                previewButton.setText("Previewing");
                previewButton.setDisable(true);
                refreshButton.setDisable(true);
                abortButton.setDisable(false);
            }
            case READY -> {
                playButton.setText("Play");
                playButton.setDisable(false);
                previewButton.setText("Preview");
                previewButton.setDisable(false);
                refreshButton.setDisable(false);
                abortButton.setDisable(true);
            }
            case IDLE -> {
                playButton.setText("Play");
                playButton.setDisable(true);
                previewButton.setText("Preview");
                previewButton.setDisable(true);
                refreshButton.setDisable(true);
                abortButton.setDisable(true);
            }
        }
    }



}
