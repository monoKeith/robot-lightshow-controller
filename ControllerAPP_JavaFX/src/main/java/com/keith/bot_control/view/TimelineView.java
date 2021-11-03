package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.TimelineControl;
import com.keith.bot_control.model.BotFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;

public class TimelineView {

    private static final String FRAME_VIEW_FXML = "frame-view.fxml";

    private TimelineControl control = BotControlAPP.getBotControl().getTimelineControl();

    private ArrayList<FrameView> frames;
    
    @FXML
    protected HBox frameCollection;

    public TimelineView(){
        frames = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        control.setView(this);
        control.initView();
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

        // Initialize to display frame info
        FrameView view = fxmlLoader.getController();
        view.setController(this);
        view.setFrame(frame);
        // Store view
        frames.add(view);
    }

    public void selectFrame(BotFrame frame){
        control.selectFrame(frame);
    }

    public void refreshFrames(){
        for (FrameView frame: frames){
            frame.refresh();
        }
    }

}
