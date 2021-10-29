package com.keith.bot_control.model;

import java.util.*;

public class BotFrame {
    private Set<BotPixel> pixels;

    public BotFrame(){
//        pixels = new HashSet<>();
        pixels = testPixels();
    }

    public static Set<BotPixel> testPixels(){
        Set<BotPixel> pixels = new HashSet<>();
        pixels.add(new BotPixel(100, 150));
        pixels.add(new BotPixel(200, 450));
        return pixels;
    }

    public Set<BotPixel> getPixels(){
        return pixels;
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
