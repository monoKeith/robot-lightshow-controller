package com.keith.bot_control.model;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import static com.keith.bot_control.controller.DotsCanvasControl.*;

public class BotPixel {

    // Constants: drawing pixels on canvas
    public static final int PIXEL_R = 16;
    public static final int PIXEL_D = 2 * PIXEL_R;
    public static final int PIXEL_SELECT_R = PIXEL_R + 4;
    public static final int PIXEL_SELECT_D = 2 * PIXEL_SELECT_R;

    public static final Point2D DEFAULT_LOCATION = new Point2D(0,0);
    public static final Color DEFAULT_COLOR = Color.ORANGE;

    private Point2D physicalLocation;
    private Point2D pixelLocation;
    private Color color;

    public BotPixel(){
        setPhysicalLocation(DEFAULT_LOCATION);
        color = DEFAULT_COLOR;
    }

    // Unit of coordinates of a pixel is meter (should match location unit in Webots)
    public BotPixel(double x, double y){
        setPixelLocation(new Point2D(x, y));
        color = DEFAULT_COLOR;
    }

    /* Unit Converters */

    private static Point2D convertToPixel(Point2D botLocation){
        double x = (botLocation.getX() / PLAYGROUND_SIZE) * CANVAS_RESOLUTION;
        double y = (- botLocation.getY() / PLAYGROUND_SIZE) * CANVAS_RESOLUTION;
        int offset = CANVAS_RESOLUTION / 2;
        return new Point2D(x + offset, y + offset);
    }

    private static Point2D convertToMeter(Point2D botLocation){
        int offset = CANVAS_RESOLUTION / 2;
        double x = (botLocation.getX() - offset) / CANVAS_RESOLUTION * PLAYGROUND_SIZE;
        double y = - (botLocation.getY() - offset) / CANVAS_RESOLUTION * PLAYGROUND_SIZE;
        return new Point2D(x, y);
    }

    /* Setters and Getters */

    public void setPhysicalLocation(Point2D physicalLocation) {
        this.physicalLocation = physicalLocation;
        this.pixelLocation = convertToPixel(physicalLocation);
    }

    public void setPixelLocation(Point2D pixelLocation) {
        this.pixelLocation = pixelLocation;
        this.physicalLocation = convertToMeter(pixelLocation);
    }

    public Point2D getPhysicalLocation(){
        return physicalLocation;
    }

    public Point2D getPixelLocation() {
        return pixelLocation;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public String toString(){
        return String.format("BotPixel @ Physical [%.2f, %.2f] @ Canvas [%d, %d]",
                physicalLocation.getX(), physicalLocation.getY(),
                (int) pixelLocation.getX(), (int) pixelLocation.getY());
    }

    /* Pixel Selection */

    public boolean containsPixel(Point2D point){
        double diffX = point.getX() - pixelLocation.getX();
        double diffY = point.getY() - pixelLocation.getY();
        double distanceFromCenter = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
        return distanceFromCenter <= PIXEL_R;
    }

}
