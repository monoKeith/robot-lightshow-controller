package com.keith.bot_control.model;

import java.util.*;

public class BotFrame {

    private String name;
    private Set<BotPixel> pixels;

    public BotFrame(){
//        pixels = new HashSet<>();
        name = "default";
        pixels = testPixels();
    }

    public static Set<BotPixel> testPixels(){
        Set<BotPixel> pixels = new HashSet<>();
//        pixels.add(new BotPixel(0, 0));
        pixels.add(new BotPixel(400, 400));
//        pixels.add(new BotPixel(-0.4, 0.4));
        return pixels;
    }

    public Set<BotPixel> getPixels(){
        return pixels;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

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

}
