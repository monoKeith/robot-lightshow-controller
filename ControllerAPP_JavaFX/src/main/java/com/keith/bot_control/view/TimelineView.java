package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.TimelineControl;
import com.keith.bot_control.model.BotFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimelineView {

    private static final String FRAME_VIEW_FXML = "frame-view.fxml";

    private TimelineControl control = BotControlAPP.getBotControl().getTimelineControl();

    private ArrayList<FrameView> frameViews;
    private Map<FrameView, Node> frameView_Node_Map;
    private Map<BotFrame, FrameView> botFrame_View_Map;
    
    @FXML
    protected HBox frameCollection;

    public TimelineView(){
        frameViews = new ArrayList<>();
        frameView_Node_Map = new HashMap<>();
        botFrame_View_Map = new HashMap<>();
    }

    @FXML
    public void initialize() {
        control.setView(this);
        control.initView();
    }

    // ONLY apply new frame to data structures, does NOT apply new frame to view
    private Node initNewFrame(BotFrame frame, int index){
        FXMLLoader fxmlLoader = BotControlAPP.loadResource(FRAME_VIEW_FXML);
        Node frameNode;
        try {
            frameNode = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Initialize to display frame info
        FrameView view = fxmlLoader.getController();
        view.setController(this);
        view.setFrame(frame);
        // Store view, [view -> Node] map, [BotFrame -> view] map
        frameViews.add(index, view);
        frameView_Node_Map.put(view, frameNode);
        botFrame_View_Map.put(frame, view);
        // Done, ready to update view
        return frameNode;
    }

    public void appendNewFrame(BotFrame frame){
        // Append frame to collection for display
        frameCollection.getChildren().add(initNewFrame(frame, frameViews.size()));
    }

    public void insertNewFrame(BotFrame frame, int index){
        initNewFrame(frame, index);
        alignFrameViewOrder();
    }

    public void alignFrameViewOrder(){
        // Clear all currently displayed frames from view
        frameCollection.getChildren().clear();
        // Add all frames to display
        for (FrameView frameView: frameViews){
            Node frameNode = frameView_Node_Map.get(frameView);
            frameCollection.getChildren().add(frameNode);
        }
    }

    public boolean frameExist(BotFrame frame){
        return botFrame_View_Map.containsKey(frame);
    }

    // Call by FrameView when a view a clicked
    public void selectFrame(BotFrame frame){
        control.selectFrame(frame);
    }

    public void refreshAllFrames(){
        for (FrameView frame: frameViews){
            frame.refresh();
        }
    }

    public void refreshSelection(){
        for (FrameView frame: frameViews){
            frame.refreshSelection();
        }
    }

    public void refreshFrame(BotFrame frame){
        FrameView view = botFrame_View_Map.get(frame);
        view.refresh();
    }

}
