package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.view.DotsView;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
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

    /* Getter and Setter */

    public void setView(DotsView view) {
        this.view = view;
        CANVAS_RESOLUTION = (int) view.getCanvas().getWidth();
    }


    /* Mouse Event Controls */

    public void mouseClick(Point2D point){
        BotPixel newSelection = null;
        // Update selected pixel
        for (BotPixel pixel: control.getCurrentFrame().getPixels()){
            if (pixel.containsPixel(point)){
                newSelection = pixel;
                break;
            }
        }
        // Reset, Select, deSelect
        if (newSelection == null){
            control.clearSelectedPixels();
            log("Reset pixel selection");
        } else if (control.selectPixel(newSelection)){
            log("select pixel: " + newSelection);
        } else {
            control.deSelectPixel(newSelection);
            log("de-select pixel: " + newSelection);
        }
        // Refresh
        refreshView();
    }

    // Update view to preview mouse dragging behavior
    // arg: delta of mouse movement from starting position
    public void mouseDragging(Dimension2D delta){


    }

    // Finalize dragging behavior when it's completed
    // arg: delta of mouse movement from starting position
    public void mouseDragReleased(Dimension2D delta){
        for (BotPixel pixel: control.getSelectedPixels()){
            Point2D oldLocation = pixel.getPixelLocation();
            Point2D newLocation = new Point2D(
                    oldLocation.getX() + delta.getWidth(),
                    oldLocation.getY() + delta.getHeight());
            pixel.setPixelLocation(newLocation);
        }
        // Refresh
        refreshView();
    }


    /* View controls */

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

            log("refresh canvas");
        });
    }

    /* Logging */

    private void log(String msg){
        System.out.println(String.format("[%s] %s", getClass().getSimpleName() , msg));
    }

}
