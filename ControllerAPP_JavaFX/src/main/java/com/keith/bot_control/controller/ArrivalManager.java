package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import javafx.geometry.Point2D;

import java.util.*;

public class ArrivalManager {
    private static final double posAccuracy = 0.02;
    // Async class to manage LightBots arrival conditions
    private Set<UUID> pending;
    private Map<UUID, BotPixel> pixelMap;
    private boolean initialized;

    public ArrivalManager(){
        reset();
    }

    public synchronized void reset(){
        initialized = false;
    }

    public synchronized void setPending(Map<UUID, BotPixel> pixelMap) {
        this.pixelMap = pixelMap;
        this.pending = pixelMap.keySet();
        initialized = true;
        notifyAll();
    }

    public synchronized void arrive(UUID uuid, Point2D point){
        if (!initialized) return;
        BotPixel pixel = pixelMap.get(uuid);
        double distance = pixel.getPhysicalLocation().distance(point);
        if (distance > posAccuracy) return;
        // Arrived
        pending.remove(uuid);
        log(String.format("Bot arrived: %s", uuid));
        notifyAll();
    }

    // Wait for ALL pending UUIDs to arrive
    public synchronized void waitForArrival(){
        while(!initialized || !pending.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        notifyAll();
    }

    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName(), msg);
    }

}
