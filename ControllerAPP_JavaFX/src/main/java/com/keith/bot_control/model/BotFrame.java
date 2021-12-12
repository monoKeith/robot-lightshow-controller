package com.keith.bot_control.model;

import com.google.gson.annotations.Expose;
import com.keith.bot_control.controller.DotsCanvasControl;
import javafx.scene.paint.Color;

import java.util.*;

public class BotFrame {

    public static final int PIXEL_PER_LINE = 30;

    public String name;
    public Set<BotPixel> pixels;
    public boolean selected;
    // Pixels selected in canvas
//    @Expose(serialize = false, deserialize = false)
    public final Set<BotPixel> selectedPixels;

    // Map UUID of connected bots -> BotPixel ID
    @Expose(serialize = false, deserialize = false)
    public static Map<Integer, UUID> pixelIdMap = new HashMap<>();

    public BotFrame(String name){
        this.name = name;
        pixels = new HashSet<>();
        selected = false;
        selectedPixels = new HashSet<>();
    }

    public BotFrame clone(){
        BotFrame newFrame = new BotFrame(this.getName());
        // Copy pixels
        for (BotPixel pixel: this.pixels){
            newFrame.pixels.add(pixel.clone());
        }
        return newFrame;
    }

    public static BotFrame sampleFrame(String name){
        BotFrame frame = new BotFrame(name);
//        frame.pixels = randomPixels(5);
        frame.pixels = pixelsInLine();
        return frame;
    }

    public static Set<BotPixel> randomPixels(int size){
        Set<BotPixel> pixels = new HashSet<>();
        Random random = new Random();
        for (int id = 0; id < size; id ++){
            pixels.add(new BotPixel(random.nextInt(650) + 50, random.nextInt(650) + 50, Color.ORANGE, id));
        }
        return pixels;
    }

    public static Set<BotPixel> pixelsInLine(){
        Set<BotPixel> pixels = new HashSet<>();
        int spacing = (DotsCanvasControl.CANVAS_RESOLUTION - 50) / PIXEL_PER_LINE;
        int y = 50;
        // Generate one line of pixels
        for (int id = 0; id < PIXEL_PER_LINE; id ++){
            BotPixel newPixel = new BotPixel(spacing * id + 50, y, Color.ORANGE, id);
            pixels.add(newPixel);
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
