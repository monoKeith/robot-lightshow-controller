package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.view.DotsView;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import static com.keith.bot_control.model.BotPixel.*;

public class DotsCanvasControl {


    // resolution of the canvas, default 720x720, must be square for now
    public static int CANVAS_RESOLUTION = 720;
    // size of playground in Webots, must be square for now (unit: meter)
    public static int PLAYGROUND_SIZE = 1;

    private final BotControl control;
    private DotsView view;
    private Boolean showPreviewPixels;

    public DotsCanvasControl(BotControl botControl){
        this.control = botControl;
        this.view = null;
        this.showPreviewPixels = false;
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
            if (control.getSelectedPixels().isEmpty()) return;
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
        for (BotPixel pixel: control.getSelectedPixels()){
            Point2D oldLocation = pixel.getPixelLocation();
            Point2D newLocation = new Point2D(
                    oldLocation.getX() + delta.getWidth(),
                    oldLocation.getY() + delta.getHeight());
            pixel.setPixelPreviewLocation(newLocation);
        }
        // Refresh
        showPreviewPixels = true;
        refreshView();
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
        showPreviewPixels = false;
        refreshView();
    }


    /* View controls */

    public void refreshView(){
        Platform.runLater(() -> {
            // Canvas
            view.clearCanvas();
            GraphicsContext gc = view.getCanvas().getGraphicsContext2D();

            // Each BotPixel
            for (BotPixel pixel: control.getCurrentFrame().getPixels()){
                Point2D canvasPixel = pixel.getPixelLocation();
                double x = canvasPixel.getX();
                double y = canvasPixel.getY();

                // Draw a ring around selected pixels
                if (control.pixelIsSelected(pixel)){
                    gc.setFill(SELECTION_RING_COLOR);
                    gc.fillOval(x - PIXEL_SELECT_R, y - PIXEL_SELECT_R, PIXEL_SELECT_D, PIXEL_SELECT_D);
                }

                // Draw center of BotPixel
                gc.setFill(pixel.getColor());
                gc.fillOval(x - PIXEL_R, y - PIXEL_R, PIXEL_D, PIXEL_D);

                // Preview dragging location when dragging selected pixels
                if (showPreviewPixels && control.pixelIsSelected(pixel)){
                    Point2D previewLocation = pixel.getPixelPreviewLocation();
                    double preview_x = previewLocation.getX();
                    double preview_y = previewLocation.getY();
                    gc.setFill(pixel.getPreviewColor());
                    gc.fillOval(preview_x - PIXEL_R, preview_y - PIXEL_R, PIXEL_D, PIXEL_D);
                }
            }
        });
    }

    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName() , msg);
    }

}
