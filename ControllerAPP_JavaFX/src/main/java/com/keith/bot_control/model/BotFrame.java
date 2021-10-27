package com.keith.bot_control.model;

import java.util.HashSet;
import java.util.Set;

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

}
