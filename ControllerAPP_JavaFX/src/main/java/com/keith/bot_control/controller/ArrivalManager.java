package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import javafx.geometry.Point2D;

import java.util.*;

public class ArrivalManager {
    private static final double posAccuracy = 0.05;
    // Async class to manage LightBots arrival conditions
    private final Map<UUID, Point2D> locationMap;
    private Set<UUID> pending;
    private boolean initialized;
    private boolean quitWaiting;

    public ArrivalManager(){
        locationMap = new HashMap<>();
        pending = new HashSet<>();
        reset();
    }

    public synchronized void reset(){
        quitWaiting = true;
        initialized = false;
        pending.clear();
        notifyAll();
    }

    public synchronized void setPending(Map<BotPixel, UUID> pixelMap) {
        locationMap.clear();
        pixelMap.forEach((pixel, uuid) -> locationMap.put(uuid, pixel.getPixelLocation()));
        this.pending = locationMap.keySet();
        initialized = true;
        quitWaiting = false;
        notifyAll();
    }

    public synchronized void arrive(UUID uuid, Point2D curLocation){
        if (!initialized) return;
        Point2D targetLocation = locationMap.get(uuid);
        if (targetLocation == null) {
            log(String.format("UNEXPECTED arrival from: %s", uuid));
            return;
        }
//        double distance = targetLocation.distance(curLocation);
//        if (distance > posAccuracy) {
//            log(String.format("WARNING! misaligned LightBot: %s", uuid));
//            return;
//        }
        // Arrived
        pending.remove(uuid);
        log(String.format("remaining: %d, just arrived: %s", pending.size(), uuid));
        notifyAll();
    }

    // Wait for ALL pending UUIDs to arrive
    public synchronized void waitForArrival(){
        while(!pending.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (quitWaiting){
                log("wait for arrival aborted");
                break;
            }
        }
        // Stop accepting arrive() once complete
        reset();
    }

    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName(), msg);
    }

}
