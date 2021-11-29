package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import javafx.geometry.Point2D;

import java.util.*;

public class ArrivalManager {
    private static final double posAccuracy = 0.02;
    // Async class to manage LightBots arrival conditions
    private Set<UUID> pending;
    private Map<UUID, Point2D> locationMap;
    private boolean initialized;

    public ArrivalManager(){
        reset();
    }

    public synchronized void reset(){
        initialized = false;
    }

    public synchronized void setPending(Map<BotPixel, UUID> pixelMap) {
        this.locationMap = new HashMap<UUID, Point2D>();
        for (Map.Entry<BotPixel, UUID> entry: pixelMap.entrySet()){
            Point2D location = entry.getKey().getPhysicalLocation();
            UUID uuid = entry.getValue();
            locationMap.put(uuid, location);
        }
        this.pending = this.locationMap.keySet();
        initialized = true;
        notifyAll();
    }

    public synchronized void arrive(UUID uuid, Point2D curLocation){
        if (!initialized) return;
        Point2D targetLocation = locationMap.get(uuid);
        if (targetLocation == null) {
            log(String.format("unexpected arrival from: %s", uuid));
            return;
        }
        double distance = targetLocation.distance(curLocation);
        if (distance > posAccuracy) {
            log(String.format("WARNING! misaligned LightBot: %s", uuid));
            return;
        }
        // Arrived
        pending.remove(uuid);
        log(String.format("LightBot arrived: %s", uuid));
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
        // Stop accepting arrive() once complete
        reset();
        notifyAll();
    }

    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName(), msg);
    }

}
