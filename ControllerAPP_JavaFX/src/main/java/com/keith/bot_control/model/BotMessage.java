package com.keith.bot_control.model;

import java.util.UUID;

public class BotMessage {

    private String topic;
    private String message;

    public BotMessage(String topic, String message){
        this.topic = topic;
        this.message = message;
    }

    // Create new target message to bot with provided UUID
    public static BotMessage newTarget(UUID uuid, double x, double y){
        String topic = String.format("T/LightBot/%s", uuid.toString());
        String message = String.format("TARGET %f %f", x, y);
        return new BotMessage(topic, message);
    }

    public String getTopic(){
        return topic;
    }

    public String getMessage(){
        return message;
    }

    public String toString(){
        return String.format("Topic: %s Message: %s", topic, message);
    }

}
