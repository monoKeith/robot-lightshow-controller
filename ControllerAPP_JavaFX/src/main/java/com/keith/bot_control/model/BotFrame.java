package com.keith.bot_control.model;

import javafx.scene.paint.Color;

import java.util.*;

public class BotFrame {

    private String name;
    private Set<BotPixel> pixels;
    private boolean selected;
    // Pixels selected in canvas
    private final Set<BotPixel> selectedPixels;

    public BotFrame(String name){
        this.name = name;
        pixels = new HashSet<>();
        selected = false;
        selectedPixels = new HashSet<>();
    }

    public static BotFrame sampleFrame(String name){
        BotFrame frame = new BotFrame(name);
        frame.pixels = testPixels();
        return frame;
    }

    public static Set<BotPixel> testPixels(){
        Set<BotPixel> pixels = new HashSet<>();
        Random random = new Random();
        for (int id = 0; id < 5; id ++){
            pixels.add(new BotPixel(random.nextInt(650) + 50, random.nextInt(650) + 50, Color.ORANGE, id));
        }
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

    public Set<BotPixel> getSelectedPixels(){
        return selectedPixels;
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
