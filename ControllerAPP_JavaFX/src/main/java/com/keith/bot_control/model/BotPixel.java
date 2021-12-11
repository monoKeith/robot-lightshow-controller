package com.keith.bot_control.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.keith.bot_control.view.FrameView;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;

import static com.keith.bot_control.controller.DotsCanvasControl.*;

public class BotPixel {

    // Constants: drawing pixels on canvas
    public static final int PIXEL_R = 16;
    public static final int PIXEL_D = 2 * PIXEL_R;
    public static final int PIXEL_CIRCLE_R = PIXEL_R + 2;
    public static final int PIXEL_CIRCLE_D = 2 * PIXEL_CIRCLE_R;
    public static final int PIXEL_SELECT_R = PIXEL_R + 5;
    public static final int PIXEL_SELECT_D = 2 * PIXEL_SELECT_R;
    // Constants: drawing pixels on Timeline
    public static final double T_PIXEL_R = 2.5;
    public static final double T_PIXEL_D = 2 * T_PIXEL_R;

    // Default values
    public static final Point2D DEFAULT_LOCATION = new Point2D(0,0);
    public static final Color DEFAULT_COLOR = Color.ORANGE;
    public static final Color SELECTION_RING_COLOR = Color.web("#ff007b");

    // Pixel Properties
    public Point2D pixelLocation;
    public Point2D physicalLocation;
    public Point2D pixelPreviewLocation;
    public Point2D pixelTimelineLocation;
    public Color color;
    public Color previewColor;
    // Mapping related
    public int pixelId;

    public BotPixel(double x, double y, Color color, int Id){
        setPixelLocation(new Point2D(x, y));
        setColor(color);
        this.pixelId = Id;
    }

    public BotPixel clone(){
        Color color = Color.color(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
        return new BotPixel(this.pixelLocation.getX(), this.pixelLocation.getY(), color, this.pixelId);
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

    private void updateTimelineLocation(Point2D botLocation){
        double x = (botLocation.getX() / CANVAS_RESOLUTION) * FrameView.T_CANVAS_RESOLUTION;
        double y = (botLocation.getY() / CANVAS_RESOLUTION) * FrameView.T_CANVAS_RESOLUTION;
        pixelTimelineLocation = new Point2D(x, y);
    }

    /* Setters and Getters */

    private Point2D limitToCanvas(Point2D pixelLocation){
        double x = pixelLocation.getX();
        double y = pixelLocation.getY();
        x = Math.max(x, PIXEL_SELECT_R);
        x = Math.min(x, CANVAS_RESOLUTION - PIXEL_SELECT_R);
        y = Math.max(y, PIXEL_SELECT_R);
        y = Math.min(y, CANVAS_RESOLUTION - PIXEL_SELECT_R);
        return new Point2D(x, y);
    }

    public void setPhysicalLocation(Point2D physicalLocation) {
        setPixelLocation(convertToPixel(physicalLocation));
    }

    public void setPixelLocation(Point2D pixelLocation) {
        pixelLocation = limitToCanvas(pixelLocation);
        this.pixelLocation = pixelLocation;
        this.pixelPreviewLocation = pixelLocation;
        this.physicalLocation = convertToMeter(pixelLocation);
        updateTimelineLocation(pixelLocation);
    }

    public void setPixelPreviewLocation(Point2D pixelPreviewLocation){
        this.pixelPreviewLocation = limitToCanvas(pixelPreviewLocation);
    }

    public Point2D getPhysicalLocation(){
        return physicalLocation;
    }

    public Point2D getPixelLocation() {
        return pixelLocation;
    }

    public Point2D getPixelPreviewLocation() {
        return pixelPreviewLocation;
    }

    public Point2D getPixelTimelineLocation() {
        return pixelTimelineLocation;
    }

    public int getPixelId() {
        return pixelId;
    }

    public void setColor(Color color){
        this.color = color;
        // Preview pixel as same color but with 35% opacity
        this.previewColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.35);
    }

    public Color getColor(){
        return color;
    }

    public Color getPreviewColor(){
        return previewColor;
    }

    public String toString(){
        return String.format("BotPixel @ Physical [%.2f, %.2f] @ Canvas [%d, %d]",
                physicalLocation.getX(), physicalLocation.getY(),
                (int) pixelLocation.getX(), (int) pixelLocation.getY());
    }

    /* Pixel Selection */

    public boolean containsPixel(Point2D point){
        return pixelLocation.distance(point) <= PIXEL_R;
    }

}
