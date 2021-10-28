package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotFrame;
import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.view.DotsView;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DotsCanvasControl {

    static final int PIXEL_RADIUS = 10;
//    final int PIXEL_COUNT;
    DotsView view;
    BotFrame frame;

    public DotsCanvasControl(){
//        PIXEL_COUNT = 0;
        this.view = null;
//        this.frame = null;
        this.frame = new BotFrame();
    }

    public void setView(DotsView view) {
        this.view = view;
    }

    public void setFrame(BotFrame frame) {
        this.frame = frame;
    }

    public void refreshView(){
        if(frame == null) return;
        Platform.runLater(() -> {
            // Canvas
            GraphicsContext gc = view.getCanvas().getGraphicsContext2D();

            // Fill pixels
            for (BotPixel pixel: frame.getPixels()){
                gc.setFill(pixel.getColor());
                double x = pixel.getLocation().getX();
                double y = pixel.getLocation().getY();
                gc.fillOval(x - PIXEL_RADIUS, y - PIXEL_RADIUS, PIXEL_RADIUS, PIXEL_RADIUS);
            }

            System.out.println("updated canvas");
        });
    }


}
