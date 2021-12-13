package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotFrame;
import com.keith.bot_control.model.BotMessage;
import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.model.TransmitterMQTT;
import javafx.geometry.Point2D;
import javafx.stage.Stage;

import java.util.*;

public class BotControl {
    private final UUID uuid;

    // Stage for opening new dialog
    private Stage stage;

    // Controllers
    private final ConnectionControl connectionControl;
    private final DotsCanvasControl dotsCanvasControl;
    private final GlobalOptionControl globalControl;
    private final PropertiesControl propertiesControl;
    private final TimelineControl timelineControl;

    // Managers
    private final ArrivalManager arrivalManager;
    private final FramesManager framesManager;

    // States
    private ConnectionControl.State connectionState;
    private GlobalOptionControl.State globalState;
    private boolean showPixelId;

    // Message processor
    private Thread msgProcessor;
    private Boolean msgProcessorStopSignal;

    // UUIDs of connected bots
    private int connectedBotsCount;


    public BotControl() {
        uuid = UUID.randomUUID();
        // Initialize all controllers
        connectionControl = new ConnectionControl(this);
        dotsCanvasControl = new DotsCanvasControl(this);
        globalControl = new GlobalOptionControl(this);
        propertiesControl = new PropertiesControl(this);
        timelineControl = new TimelineControl(this);
        // Manager
        arrivalManager = new ArrivalManager();
        framesManager = new FramesManager();
        // Initial states
        connectionState = ConnectionControl.State.DISCONNECTED;
        globalState = GlobalOptionControl.State.IDLE;
        // Init vars
        connectedBotsCount = 0;
        showPixelId = true;
        // Init message processor
        initMsgProcessor();
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

    public int getConnectedBotsCount(){
        return connectedBotsCount;
    }

    public ArrayList<BotFrame> getFrames(){
        return framesManager.getFrames();
    }

    public boolean getShowPixelId(){
        return showPixelId;
    }

    public void setShowPixelId(boolean val){
        showPixelId = val;
        dotsCanvasControl.refreshView();
    }

    public void airTimeUpdate(double time){
        getCurrentFrame().setAirTime(time);
        propertiesControl.refreshFrameProperties();
    }

    public BotFrame getCurrentFrame(){
        return framesManager.getCurrentFrame();
    }

    // Switch to next frame, return false if there's no next frame
    public boolean nextFrame(){
        if (framesManager.nextFrame()){
            selectedFrameChanged();
            return true;
        }
        return false;
    }

    /* Setter */

    public void setStage(Stage stage){
        this.stage = stage;
    }


    /* Bot Pixel selection */

    public Set<BotPixel> getSelectedPixels() {
        return getCurrentFrame().getSelectedPixels();
    }

    public void clearSelectedPixels() {
        getCurrentFrame().getSelectedPixels().clear();
        propertiesControl.refreshView();
    }

    public boolean selectPixel(BotPixel newSelection) {
        if (getCurrentFrame().getSelectedPixels().add(newSelection)){
            propertiesControl.refreshView();
            return true;
        }
        return false;
    }

    public void deSelectPixel(BotPixel pixel){
        getCurrentFrame().getSelectedPixels().remove(pixel);
        propertiesControl.refreshView();
    }

    public boolean pixelIsSelected(BotPixel pixel) {
        return getCurrentFrame().getSelectedPixels().contains(pixel);
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
                arrivalManager.reset();
                connectedBotsCount = 0;
                BotFrame.clearConnectedBots();
                propertiesControl.refreshConnectedBots();
                updateGlobalState(GlobalOptionControl.State.IDLE);
            }
        }
        connectionControl.refreshView();
        log("connection state updated:" + getConnectionState());
    }

    public void updateGlobalState(GlobalOptionControl.State state){
        if (globalState == state) return;
        globalState = state;
        globalControl.refreshView();
        log("global state updated: " + getGlobalState());
    }

    // Called by DotsCanvasControl to update properties when BotPixel moved
    public void updateBotPixelLocation(){
        propertiesControl.refreshView();
        timelineControl.refreshCurrentFrame();
    }

    // Called by Properties Control when properties of BotPixel changed
    public void updateBotPixelProperties(){
        dotsCanvasControl.refreshView();
        timelineControl.refreshCurrentFrame();
    }

    // Called by TimelineControl when another frame is selected
    public void setCurrentFrame(BotFrame frame){
        framesManager.setCurrentFrame(frame);
        selectedFrameChanged();
    }

    // Refresh related views when selected frame changed
    private void selectedFrameChanged(){
        globalControl.refreshView();
        dotsCanvasControl.refreshView();
        propertiesControl.refreshView();
        timelineControl.refreshSelection();
    }

    // Called by Properties Control when properties of current Frame changed
    public void updateCurrentFrameProperties(){
        globalControl.refreshView();
        propertiesControl.refreshFrameProperties();
        timelineControl.refreshCurrentFrame();
    }


    /* Save & Load */

    public void load(){
        framesManager.load(stage);
        // Update view
        timelineControl.initView();
        selectedFrameChanged();
    }

    public void save(){
        framesManager.save(stage);
    }


    /* Bot Frame related */

    // Copy current frame, add to next index of current frame.
    // Change selected frame to the new frame
    public void duplicateCurrentFrame(){
        framesManager.duplicateCurrentFrame();
        // Update timeline to include new frame
        timelineControl.addMissingFrames();
        // Done
        nextFrame();
    }

    // Delete current frame and update selected frame
    public void deleteCurrentFrame(){
        BotFrame currentFrame = framesManager.getCurrentFrame();
        // Abort if frameManager reject
        if (!framesManager.deleteCurrentFrame()) return;
        timelineControl.removeFrame(currentFrame);
        // Done
        selectedFrameChanged();
    }

    public void rearrangeCurrentFrame(boolean toLeft){
        // Abort if frameManager reject
        if (!framesManager.rearrangeCurrentFrame(toLeft)) return;
        // Apply changes to view
        timelineControl.syncFramesOrder();
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
                BotFrame.recordConnection(msg.msgUUID(), msg.botLocation());
                connectedBotsCount += 1;
                globalControl.refreshView();
                propertiesControl.refreshConnectedBots();
            }
            case TransmitterMQTT.ARRIVAL_TOPIC -> {
                arrivalManager.arrive(msg.msgUUID(), msg.botLocation());
            }
        }
    }


    /* Bot Control Functions */

    // When playing animation or previewing frame
    public void abort(){
        log("play/preview abort");
        arrivalManager.reset();
        updateGlobalState(GlobalOptionControl.State.READY);
    }

    // Reset connected bots, ask all bots to report UUID
    public void refreshConnections(){
        if (getGlobalState() != GlobalOptionControl.State.READY) return;
        log("refresh LightBot connections");
        connectedBotsCount = 0;
        globalControl.refreshView();
        propertiesControl.refreshConnectedBots();
        BotMessage msg = BotMessage.reportUUID();
        connectionControl.publishMessage(msg);
    }

    // Tell all bots to goto location on current frame
    public void previewFrame(){
        if (getGlobalState() != GlobalOptionControl.State.READY) return;
        updateGlobalState(GlobalOptionControl.State.PREVIEW);
        publishTargets();
        log("preview wait for arrival");
        arrivalManager.waitForArrival();
        log("preview complete");
        updateGlobalState(GlobalOptionControl.State.READY);
    }

    // Start playing animation from current frame.
    public void playFromCurrentFrame(){
        if (getGlobalState() != GlobalOptionControl.State.READY) return;
        updateGlobalState(GlobalOptionControl.State.PLAYING);
        do {
            publishTargets();
            arrivalManager.waitForArrival();
            // Quit signal
            if (getGlobalState() != GlobalOptionControl.State.PLAYING) continue;
            // Wait airtime
            double airTime = getCurrentFrame().getAirTime();
            if (airTime <= 0) continue;
            log("waiting airtime");
            try {
                Thread.sleep((long) (getCurrentFrame().getAirTime() * 1000));
            } catch (InterruptedException e) {
                log("interrupted when waiting for airtime");
                e.printStackTrace();
            }
        } while ((getGlobalState() == GlobalOptionControl.State.PLAYING) && nextFrame());
        // Loop exited but not with state PLAYING
        if (getGlobalState() != GlobalOptionControl.State.PLAYING) {
            log("play thread aborted");
            return;
        }
        updateGlobalState(GlobalOptionControl.State.READY);
    }

    // Send message to all bots, update target location
    private void publishTargets(){
        Map<BotPixel, UUID> targetMap = getCurrentFrame().getTargetMap();
        arrivalManager.setPending(targetMap);
        log("publish targets count: " + targetMap.size());
        // Send target message
        targetMap.forEach((pixel, uuid) -> {
            Point2D target = pixel.getPhysicalLocation();
            // Generate and send message
            BotMessage message = new BotMessage(uuid);
            message.newTarget(target.getX(), target.getY());
            message.setColor(pixel.getColor());
            connectionControl.publishMessage(message);
        });
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
