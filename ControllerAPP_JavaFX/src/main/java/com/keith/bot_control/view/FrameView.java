package com.keith.bot_control.view;

import com.keith.bot_control.model.BotFrame;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class FrameView {

    private TimelineView controller;
    private BotFrame frame;

    private static final String UNSELECTED_STYLE = """
        -fx-background-color: #656a8f; 
        -fx-background-insets: 2; 
        -fx-border-color: #656a8f; 
        -fx-border-radius: 5; 
        -fx-border-width: 2;
    """;

    private static final String SELECTED_STYLE = """
        -fx-background-color: #656a8f; 
        -fx-background-insets: 2; 
        -fx-border-color: white; 
        -fx-border-radius: 5; 
        -fx-border-width: 2;
    """;

    @FXML
    protected Pane pane;

    @FXML
    protected Label frameName;

    @FXML
    protected void initialize(){
    }

    @FXML
    protected void click(){
        controller.selectFrame(frame);
    }

    public void setController(TimelineView controller){
        this.controller = controller;
    }

    public void setFrame(BotFrame frame){
        this.frame = frame;
        refresh();
    }

    // Update view according to frame
    public void refresh(){
        // Border style
        if (frame.isSelected()){
            pane.setStyle(SELECTED_STYLE);
        } else {
            pane.setStyle(UNSELECTED_STYLE);
        }
        // Frame name
        frameName.setText(frame.getName());
    }
}
