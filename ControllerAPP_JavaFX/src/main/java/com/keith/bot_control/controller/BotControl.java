package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotFrame;
import com.keith.bot_control.model.BotMessage;
import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.model.TransmitterMQTT;
import javafx.geometry.Point2D;

import java.util.*;

public class BotControl {
    private final UUID uuid;

    // Controllers
    private final ConnectionControl connectionControl;
    private final DotsCanvasControl dotsCanvasControl;
    private final GlobalOptionControl globalControl;
    private final PropertiesControl propertiesControl;
    private final TimelineControl timelineControl;

    // States
    private ConnectionControl.State connectionState;
    private GlobalOptionControl.State globalState;

    private Thread msgProcessor;
    private Boolean msgProcessorStopSignal;
    // Set of UUIDs of connected bots
    private Set<UUID> connectedBots;

    private ArrayList<BotFrame> frames;
    private BotFrame currentFrame;
    // Pixels selected in canvas
    private final Set<BotPixel> selectedPixels;

    public BotControl() {
        uuid = UUID.randomUUID();
        // Initialize all controllers
        connectionControl = new ConnectionControl(this);
        dotsCanvasControl = new DotsCanvasControl(this);
        globalControl = new GlobalOptionControl(this);
        propertiesControl = new PropertiesControl(this);
        timelineControl = new TimelineControl(this);
        // Initial states
        connectionState = ConnectionControl.State.DISCONNECTED;
        globalState = GlobalOptionControl.State.IDLE;
        // Init vars
        connectedBots = new HashSet<>();
        selectedPixels = new HashSet<>();
        initCurrentFrame();
        // Init message processor
        initMsgProcessor();
    }

    private void initCurrentFrame(){
        // Testing only
        frames = new ArrayList<>();
        frames.add(BotFrame.sampleFrame("1"));
        frames.add(BotFrame.sampleFrame("2"));
        frames.add(BotFrame.sampleFrame("3"));
        frames.add(BotFrame.sampleFrame("4"));
        frames.add(BotFrame.sampleFrame("5"));
        frames.add(BotFrame.sampleFrame("6"));
        frames.add(BotFrame.sampleFrame("7"));
        frames.add(BotFrame.sampleFrame("8"));
        frames.add(BotFrame.sampleFrame("9"));
        frames.add(BotFrame.sampleFrame("10"));
        frames.add(BotFrame.sampleFrame("11"));
        frames.add(BotFrame.sampleFrame("12"));
        frames.add(BotFrame.sampleFrame("13"));
        frames.add(BotFrame.sampleFrame("14"));
        frames.add(BotFrame.sampleFrame("15"));
        frames.add(BotFrame.sampleFrame("16"));
        frames.add(BotFrame.sampleFrame("17"));
        frames.add(BotFrame.sampleFrame("18"));
        frames.add(BotFrame.sampleFrame("19"));
        frames.add(BotFrame.sampleFrame("20"));

        setCurrentFrame(frames.get(0));
    }


    /* Getters */

    public UUID getUuid(){
        return uuid;
    }

    public ConnectionControl getConnectionControl(){
        return connectionControl;
    }

    public DotsCanvasControl getDotsControl(){
        return dotsCanvasControl;
    }

    public GlobalOptionControl getGlobalControl(){
        return globalControl;
    }

    public PropertiesControl getPropertiesControl(){
        return propertiesControl;
    }

    public TimelineControl getTimelineControl(){
        return timelineControl;
    }

    public Set<UUID> getConnectedBots(){
        return connectedBots;
    }

    public ArrayList<BotFrame> getFrames(){
        return frames;
    }

    private void setCurrentFrame(BotFrame frame){
        if (currentFrame != null) currentFrame.setSelecte(false);
        currentFrame = frame;
        currentFrame.setSelecte(true);
    }

    public BotFrame getCurrentFrame(){
        return currentFrame;
    }


    /* Bot Pixel selection */

    public Set<BotPixel> getSelectedPixels() {
        return selectedPixels;
    }

    public void clearSelectedPixels() {
        selectedPixels.clear();
        propertiesControl.refreshView();
    }

    public boolean selectPixel(BotPixel newSelection) {
        if (selectedPixels.add(newSelection)){
            propertiesControl.refreshView();
            return true;
        }
        return false;
    }

    public void deSelectPixel(BotPixel pixel){
        selectedPixels.remove(pixel);
        propertiesControl.refreshView();
    }

    public boolean pixelIsSelected(BotPixel pixel) {
        return selectedPixels.contains(pixel);
    }


    /* State update & Notifiers */

    public ConnectionControl.State getConnectionState(){
        return connectionState;
    }

    public GlobalOptionControl.State getGlobalState(){
        return globalState;
    }

    public void updateConnectionState(ConnectionControl.State state){
        if (connectionState == state) return;
        connectionState = state;
        switch (connectionState){
            case CONNECTED -> {
                // Update global state to READY
                updateGlobalState(GlobalOptionControl.State.READY);
            }
            case DISCONNECTED -> {
                // Update global state to IDLE
                connectedBots = new HashSet<>();
                updateGlobalState(GlobalOptionControl.State.IDLE);
            }
        }
        connectionControl.refreshView();
    }

    public void updateGlobalState(GlobalOptionControl.State state){
        if (globalState == state) return;
        globalState = state;
        globalControl.refreshView();
    }

    // Called by DotsCanvasControl to update properties when BotPixel moved
    public void updateBotPixelLocation(){
        propertiesControl.refreshView();
    }

    // Called by Properties Control when properties of BotPixel changed
    public void updateBotPixelProperties(){
        dotsCanvasControl.refreshView();
    }

    // Called by TimelineControl when another frame is selected
    public void updateCurrentFrame(BotFrame frame){
        setCurrentFrame(frame);
        globalControl.refreshView();
        dotsCanvasControl.refreshView();
        propertiesControl.refreshView();
    }

    // Called by Properties Control when properties of Frame changed
    public void updateFrameProperties(){
        globalControl.refreshView();
        propertiesControl.refreshFrameProperties();
        timelineControl.refreshView();
    }


    /* Message processor - Receives message from transmitter and process */

    // Spawn a new thread to receive message
    private void initMsgProcessor(){
        // Do nothing if already exist
        if (msgProcessor != null) return;
        // New thread
        msgProcessorStopSignal = false;
        msgProcessor = new Thread(this::receiveMessage);
        msgProcessor.start();
    }

    private void terminateMsgProcessor(){
        if (msgProcessor == null) return;
        msgProcessorStopSignal = true;
        connectionControl.terminateMsgQueue();
        try {
            msgProcessor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage(){
        while(!msgProcessorStopSignal){
            BotMessage msg = connectionControl.waitForMsg();
            if (msg == null) continue;
            processMessage(msg);
        }
        log("message processor thread terminated");
    }

    private void processMessage(BotMessage msg){
        log("processing message: " + msg);
        switch (msg.getTopic()){
            case TransmitterMQTT.UUID_TOPIC -> {
                connectedBots.add(UUID.fromString(msg.getMessage()));
                globalControl.refreshView();
            }
        }
    }


    /* Bot Control Functions */

    // Send message to all bots, update target location
    public void publishTargets(){
        // TODO optimize location for each bot in BotFrame class!!!
        Map<UUID, BotPixel> targetMap = currentFrame.generateTargetMap(connectedBots);
        // Send target message
        for (Map.Entry<UUID, BotPixel> entry: targetMap.entrySet()){
            UUID uuid = entry.getKey();
            BotPixel pixel = entry.getValue();
            Point2D target = pixel.getPhysicalLocation();
            BotMessage message = BotMessage.newTarget(uuid, target.getX(), target.getY());
            connectionControl.publishMessage(message);
        }
    }

    public void terminate(){
        connectionControl.resetTransmitter();
        terminateMsgProcessor();
    }


    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName(), msg);
    }

}
