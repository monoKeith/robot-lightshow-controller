package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.view.DotsView;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DotsCanvasControl {

    // radius of the circle representing a bot on canvas
    static final int PIXEL_RADIUS = 10;
    // resolution of the canvas, default 720x720, must be square for now
    static int CANVAS_RESOLUTION = 720;
    // size of playground in Webots, must be square for now (unit: meter)
    static int PLAYGROUND_SIZE = 1;
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

    // Convert from meters to pixels coordinate (meter) to match canvas resolution
    private Point2D convertToPixel(Point2D botLocation){
        int offset = CANVAS_RESOLUTION / 2;
        double x = (botLocation.getX() / PLAYGROUND_SIZE) * CANVAS_RESOLUTION + offset;
        double y = (- botLocation.getY() / PLAYGROUND_SIZE) * CANVAS_RESOLUTION + offset;
        return new Point2D(x, y);
    }

    public void refreshView(){
        Platform.runLater(() -> {
            // Canvas
            GraphicsContext gc = view.getCanvas().getGraphicsContext2D();

            // Fill pixels
            for (BotPixel pixel: control.getCurrentFrame().getPixels()){
                gc.setFill(pixel.getColor());
                Point2D canvasPixel = convertToPixel(pixel.getLocation());
                double x = canvasPixel.getX();
                double y = canvasPixel.getY();
                gc.fillOval(x - PIXEL_RADIUS, y - PIXEL_RADIUS, PIXEL_RADIUS, PIXEL_RADIUS);
            }

            System.out.println("updated canvas");
        });
    }


}
