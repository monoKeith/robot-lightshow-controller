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
    private Label currentFrameName;

    @FXML
    private Button previewButton, playButton;

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
            }
            case PREVIEW -> {
                playButton.setText("Play");
                playButton.setDisable(true);
                previewButton.setText("Previewing");
                previewButton.setDisable(true);
            }
            case READY -> {
                playButton.setText("Play");
                playButton.setDisable(false);
                previewButton.setText("Preview");
                previewButton.setDisable(false);
            }
            case IDLE -> {
                playButton.setText("Play");
                playButton.setDisable(true);
                previewButton.setText("Preview");
                previewButton.setDisable(true);
            }
        }
    }

}
