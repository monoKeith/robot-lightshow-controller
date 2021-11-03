package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.TimelineControl;
import com.keith.bot_control.model.BotFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TimelineView {

    private static final String FRAME_VIEW_FXML = "frame-view.fxml";

    TimelineControl control = BotControlAPP.getBotControl().getTimelineControl();
    
    @FXML
    protected HBox frameCollection;

    public TimelineView(){

    }

    @FXML
    public void initialize() throws IOException {
        control.setView(this);
        control.refreshView();
    }

    public void addFrame(BotFrame frame){
        FXMLLoader fxmlLoader = BotControlAPP.loadResource(FRAME_VIEW_FXML);
        // Add frame to collection
        try {
            frameCollection.getChildren().add(fxmlLoader.load());

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // Set frame information
        FrameView view = fxmlLoader.getController();
        view.setController(this);
        view.setFrame(frame);
    }

    public void selectFrame(BotFrame frame){
        System.out.println("selected frame: " + frame);
    }

}
