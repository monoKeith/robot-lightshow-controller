package com.keith.bot_control.model;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.UUID;

public class BotMessage {

    private String topic;
    private StringBuilder message;

    public BotMessage(UUID uuid){
        this.topic = String.format("T/LightBot/%s", uuid.toString());
        this.message = new StringBuilder();
    }

    public BotMessage(String topic, String message){
        this.topic = topic;
        this.message = new StringBuilder();
        this.message.append(message);
    }

    // Create new target message to bot with provided UUID
    public void newTarget(double x, double y){
        String msg = String.format("TARGET %f %f|", x, y);
        message.append(msg);
    }

    public void setColor(Color color){
        String msg = String.format("COLOR %d|", getRGB(color));
        message.append(msg);
    }

    public String getTopic(){
        return topic;
    }

    public String getMessage(){
        return message.toString();
    }

    public String toString(){
        return String.format("Topic: %s Message: %s", topic, message);
    }

    // Try to extract UUID from an arrival message
    public UUID arrivalMsgUUID(){
        if (!topic.equals(TransmitterMQTT.ARRIVAL_TOPIC)) return null;
        String uuidString = message.toString().split(" ")[0];
        return UUID.fromString(uuidString);
    }

    public Point2D arrivalPoint(){
        if (!topic.equals(TransmitterMQTT.ARRIVAL_TOPIC)) return null;
        String[] msg = message.toString().split(" ");
        return new Point2D(Double.parseDouble(msg[1]), Double.parseDouble(msg[2]));
    }

    public static int getRGB(Color color){
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return (r * 65536) + (g * 256) + b;
    }

}
