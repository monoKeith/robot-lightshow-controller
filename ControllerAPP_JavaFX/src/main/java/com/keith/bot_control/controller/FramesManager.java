package com.keith.bot_control.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keith.bot_control.model.BotFrame;
import com.keith.bot_control.model.ColorAdapter;
import com.keith.bot_control.model.PointAdapter;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class FramesManager {
    private final ArrayList<BotFrame> frames;
    private int currentFrameIndex;
    private BotFrame currentFrame;

    public FramesManager(){
        frames = new ArrayList<>();
        // Only for testing, create random frames
        for (int i = 1; i <= 3; i++){
            frames.add(BotFrame.sampleFrame("Frame_" + i));
        }
        setCurrentFrame(0);

        save();

        // Test deserialize

    }


    /* Save & Load */

    public void save(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Point2D.class, new PointAdapter());
        builder.registerTypeAdapter(Color.class, new ColorAdapter());
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String aFrame = gson.toJson(frames);
        // Save to file
        log(aFrame);
    }

    /* Getters */

    public ArrayList<BotFrame> getFrames(){
        return frames;
    }

    public int getCurrentFrameIndex(){
        return currentFrameIndex;
    }

    public BotFrame getCurrentFrame(){
        return currentFrame;
    }


    /* Frame Controls */

    public void setCurrentFrame(BotFrame frame){
        if (currentFrame != null) currentFrame.setSelecte(false);
        currentFrameIndex = frames.indexOf(frame);
        currentFrame = frame;
        currentFrame.setSelecte(true);
    }

    public void setCurrentFrame(int index){
        if (currentFrame != null) currentFrame.setSelecte(false);
        currentFrameIndex = index;
        currentFrame = frames.get(index);
        currentFrame.setSelecte(true);
    }

    // set current frame to next frame, return false if there's no next frame
    public boolean nextFrame(){
        int nextFrameIndex = currentFrameIndex + 1;
        if (nextFrameIndex >= getFrames().size()) return false;
        setCurrentFrame(nextFrameIndex);
        return true;
    }

    public void duplicateCurrentFrame(){
        log("duplicate selected BotFrame");
        BotFrame newFrame = currentFrame.clone();
        frames.add(currentFrameIndex + 1, newFrame);
    }

    // delete selected frame, reject (false) when only 1 frame left
    public boolean deleteCurrentFrame(){
        // Abort if only one frame left
        if (frames.size() <= 1) return false;
        log("delete selected BotFrame");
        frames.remove(currentFrame);
        setCurrentFrame(Math.min(currentFrameIndex, (frames.size() - 1)));
        return true;
    }

    // Shift current frame to left or right by 1 position
    // Abort if the frame is already at the end of desired direction
    public boolean rearrangeCurrentFrame(boolean toLeft){
        // Abort if the frame is already at the end of desired direction
        if ((toLeft && currentFrameIndex <= 0) || (!toLeft && currentFrameIndex >= frames.size() - 1)) return false;
        log("rearrange selected BotFrame");
        // Rearrange
        frames.remove(currentFrame);
        currentFrameIndex = toLeft ? currentFrameIndex - 1 : currentFrameIndex + 1;
        frames.add(currentFrameIndex, currentFrame);
        return true;
    }


    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName(), msg);
    }
}
