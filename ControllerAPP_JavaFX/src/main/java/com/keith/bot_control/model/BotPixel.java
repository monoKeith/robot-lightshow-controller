package com.keith.bot_control.model;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class BotPixel {

    public static final Point2D DEFAULT_LOCATION = new Point2D(0,0);
    public static final Color DEFAULT_COLOR = Color.AQUA;

    private Point2D location;
    private Color color;

    public BotPixel(){
        location = DEFAULT_LOCATION;
        color = DEFAULT_COLOR;
    }

    public BotPixel(double x, double y){
        location = new Point2D(x, y);
        color = DEFAULT_COLOR;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public Point2D getLocation(){
        return location;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

}
