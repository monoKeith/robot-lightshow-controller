package com.keith.bot_control.model;

import com.google.gson.annotations.Expose;
import com.keith.bot_control.controller.DotsCanvasControl;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.*;

public class BotFrame {

    public static final int TOTAL_PIXEL_COUNT = 30;
    public static final Color DEFAULT_COLOR = Color.ORANGE;

    public String name;
    public Set<BotPixel> pixels;
    public boolean selected;
    // Pixels selected in canvas
    public Set<BotPixel> selectedPixels;

    // Map UUID of connected bots -> BotPixel ID
    @Expose(serialize = false, deserialize = false)
    public static Map<Integer, UUID> pixelIdMap = new HashMap<>();
    // ONLY for assigning pixel ID during startup
    public static Map<Double, UUID> botLocations = new HashMap<>();
    public static PriorityQueue<Double> xOrigins = new PriorityQueue<>();

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
        // Create a line of pixels at y=50
        frame.pixels = pixelsInLine(50);
        return frame;
    }

    public static Set<BotPixel> randomPixels(int size){
        Set<BotPixel> pixels = new HashSet<>();
        Random random = new Random();
        for (int id = 0; id < size; id ++){
            pixels.add(new BotPixel(random.nextInt(650) + 50, random.nextInt(650) + 50, DEFAULT_COLOR, id));
        }
        return pixels;
    }

    public static Set<BotPixel> pixelsInLine(int y){
        Set<BotPixel> pixels = new HashSet<>();
        int spacing = (DotsCanvasControl.CANVAS_RESOLUTION - 50) / TOTAL_PIXEL_COUNT;
        // Generate one line of pixels
        for (int id = 0; id < TOTAL_PIXEL_COUNT; id ++){
            BotPixel newPixel = new BotPixel(spacing * id + 50, y, Color.ORANGE, id);
            pixels.add(newPixel);
        }
        return pixels;
    }

    public static void recordConnection(UUID uuid, Point2D location){
        // Fast workaround: assign ID based on x-coordinate
        xOrigins.add(location.getX());
        botLocations.put(location.getX(), uuid);
        // When all bots connected
        if (xOrigins.size() == TOTAL_PIXEL_COUNT){
            int pixelId = 0;
            while (!xOrigins.isEmpty()){
                double xCoordinate = xOrigins.poll();
                uuid = botLocations.get(xCoordinate);
                pixelIdMap.put(pixelId++, uuid);
            }
        }
    }

    // Gets call when disconnect from msg broker
    public static void clearConnectedBots(){
        pixelIdMap.clear();
        xOrigins.clear();
        botLocations.clear();
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
