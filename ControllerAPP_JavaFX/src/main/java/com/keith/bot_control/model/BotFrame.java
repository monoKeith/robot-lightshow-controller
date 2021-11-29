package com.keith.bot_control.model;

import javafx.scene.paint.Color;

import java.util.*;

public class BotFrame {

    private String name;
    private Set<BotPixel> pixels;
    private boolean selected;
    // Pixels selected in canvas
    private final Set<BotPixel> selectedPixels;

    // Map UUID of connected bots -> BotPixel ID
    public static Map<Integer, UUID> pixelIdMap = new HashMap<>();;

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

    public static void updatePixelIdMap(Set<UUID> connectedBots){
        // TODO optimize base on location of bots
        pixelIdMap = new HashMap<>();
        int pixelId = 0;
        for (UUID uuid: connectedBots){
            pixelIdMap.put(pixelId++, uuid);
        }
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

    public Map<BotPixel, UUID> getTargetMap(){
        // Generate BotPixel -> UUID mapping
        Map<BotPixel, UUID> targetMap = new HashMap<>();
        for (BotPixel pixel: pixels){
            int id = pixel.getPixelId();
            UUID uuid = pixelIdMap.get(id);
            if (uuid == null) continue;
            targetMap.put(pixel, uuid);
        }
        return targetMap;
    }

    public String toString() {
        return String.format("BotFrame [%s]", name);
    }

}
