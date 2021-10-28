package com.keith.bot_control.model;

public class BotMessage {

    private String topic;
    private String message;

    public BotMessage(String topic, String message){
        this.topic = topic;
        this.message = message;
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
