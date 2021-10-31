package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.view.DotsView;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.keith.bot_control.model.BotPixel.*;

public class DotsCanvasControl {


    // resolution of the canvas, default 720x720, must be square for now
    public static int CANVAS_RESOLUTION = 720;
    // size of playground in Webots, must be square for now (unit: meter)
    public static int PLAYGROUND_SIZE = 1;
//    final int PIXEL_COUNT;
    private BotControl control;
    private DotsView view;

    public DotsCanvasControl(BotControl control){
//        PIXEL_COUNT = 0;
        this.control = control;
        this.view = null;
    }

    public void setView(DotsView view) {
        this.view = view;
        CANVAS_RESOLUTION = (int) view.getCanvas().getWidth();
    }

    public void mousePress(Point2D point){
        BotPixel newSelection = null;
        // Update selected pixel
        for (BotPixel pixel: control.getCurrentFrame().getPixels()){
            if (pixel.containsPixel(point)){
                newSelection = pixel;
                break;
            }
        }
        // Set selectedPixel
        if (newSelection == null){
            control.clearSelectedPixels();
            System.out.println("Reset pixel selection");
        } else {
            if (control.newPixelSelection(newSelection))
                System.out.println("Selected pixel: " + newSelection);
        }
        // Refresh
        refreshView();
    }

    public void refreshView(){
        Platform.runLater(() -> {
            // Canvas
            view.clearCanvas();
            GraphicsContext gc = view.getCanvas().getGraphicsContext2D();

            // Fill pixels
            for (BotPixel pixel: control.getCurrentFrame().getPixels()){
                Point2D canvasPixel = pixel.getPixelLocation();
                double x = canvasPixel.getX();
                double y = canvasPixel.getY();
                // Selected pixels = have a ring around it
                if (control.pixelIsSelected(pixel)){
                    gc.setFill(Color.GRAY);
                    gc.fillOval(x - PIXEL_SELECT_R, y - PIXEL_SELECT_R, PIXEL_SELECT_D, PIXEL_SELECT_D);
                }
                // Fill Bot Pixel color
                gc.setFill(pixel.getColor());
                gc.fillOval(x - PIXEL_R, y - PIXEL_R, PIXEL_D, PIXEL_D);
            }

            System.out.println("refresh canvas");
        });
    }


}
