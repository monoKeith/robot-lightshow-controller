package com.keith.bot_control.view;

import com.keith.bot_control.model.BotFrame;
import com.keith.bot_control.model.BotPixel;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import static com.keith.bot_control.model.BotPixel.T_PIXEL_D;
import static com.keith.bot_control.model.BotPixel.T_PIXEL_R;

public class FrameView {

    // resolution of the canvas on timeline, default 100x100, must be square for now
    public static final int T_CANVAS_RESOLUTION = 100;
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
    protected Canvas canvas;

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
        // Canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getHeight(), canvas.getHeight());
        for (BotPixel pixel: frame.getPixels()){
            Point2D canvasPixel = pixel.getPixelTimelineLocation();
            double x = canvasPixel.getX();
            double y = canvasPixel.getY();
            // Draw center of BotPixel
            gc.setFill(pixel.getColor());
            gc.fillOval(x - T_PIXEL_R, y - T_PIXEL_R, T_PIXEL_D, T_PIXEL_D);
        }
    }
}
