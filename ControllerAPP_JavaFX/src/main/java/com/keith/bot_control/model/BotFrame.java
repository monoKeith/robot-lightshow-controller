package com.keith.bot_control.model;

import javafx.scene.paint.Color;

import java.util.*;

public class BotFrame {

    private String name;
    private Set<BotPixel> pixels;
    private boolean selected;

    public BotFrame(String name){
        this.name = name;
        pixels = new HashSet<>();
        selected = false;
    }

    public static BotFrame sampleFrame(){
        BotFrame frame = new BotFrame("sample");
        frame.pixels = testPixels();
        return frame;
    }

    public static Set<BotPixel> testPixels(){
        Set<BotPixel> pixels = new HashSet<>();
        pixels.add(new BotPixel(200, 200, Color.ORANGE));
        pixels.add(new BotPixel(300, 300, Color.GREEN));
        pixels.add(new BotPixel(400, 400, Color.PINK));
        pixels.add(new BotPixel(200, 400, Color.AQUA));
        return pixels;
    }

    /* Getter and Setter */

    public Set<BotPixel> getPixels(){
        return pixels;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSelecte(boolean selected){
        this.selected = selected;
    }

    public boolean isSelected(){
        return selected;
    }

    /* Render */

    public Map<UUID, BotPixel> generateTargetMap(Set<UUID> bots){
        // TODO map closest pixel with bot

        // Currently, it only maps 1st bot with 1st pixel
        Map<UUID, BotPixel> targetMap = new HashMap<>();
        if (pixels.size() > 0 && bots.size() > 0){
            UUID uuid = (UUID) bots.toArray()[0];
            BotPixel pixel = (BotPixel) pixels.toArray()[0];
            targetMap.put(uuid, pixel);
        }
        return targetMap;
    }

    public String toString() {
        return String.format("BotFrame [%s]", name);
    }

}
