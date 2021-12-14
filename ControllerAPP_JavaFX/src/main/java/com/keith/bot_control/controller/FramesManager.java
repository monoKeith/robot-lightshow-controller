package com.keith.bot_control.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.keith.bot_control.model.BotFrame;
import com.keith.bot_control.model.ColorAdapter;
import com.keith.bot_control.model.PointAdapter;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FramesManager {
    private ArrayList<BotFrame> frames;
    private int currentFrameIndex;
    private BotFrame currentFrame;

    // Gson related
    private final Gson gson;

    public FramesManager(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Point2D.class, new PointAdapter());
        builder.registerTypeAdapter(Color.class, new ColorAdapter());
//        builder.setPrettyPrinting();
        gson = builder.create();

        frames = new ArrayList<>();
        // Create one frames
        for (int i = 1; i <= 1; i++){
            frames.add(BotFrame.sampleFrame("Frame_" + i));
        }
        setCurrentFrame(0);
    }


    /* Save & Load */

    public void save(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open BotFrames file (json)");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("BotFrames file", "*.json"));
        // Choose location
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile == null) {
            log("invalid file, abort");
            return;
        } else {
            try {
                selectedFile.createNewFile();
            } catch (IOException e) {
                log("failed to create new file");
                e.printStackTrace();
                return;
            }
        }
        // Save tile
        try {
            FileWriter writer = new FileWriter(selectedFile);
            parseToJSON(writer);
            writer.close();
        } catch (IOException e) {
            log("failed to save to file, abort");
            e.printStackTrace();
        }
        // Done
        log("successfully saved frames to file");
    }

    public void load(Stage stage){
        // Chooser location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open BotFrames file (json)");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("BotFrames file", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        // Load from JSON
        try {
            FileReader reader = new FileReader(selectedFile);
            loadFromJSON(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            log("failed to read file, abort");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Done
        log("successfully loaded frames from file");
    }

    private void parseToJSON(FileWriter writer){
        gson.toJson(frames, writer);
    }

    private void loadFromJSON(FileReader reader){
        Type framesType = new TypeToken<ArrayList<BotFrame>>(){}.getType();
        frames = gson.fromJson(reader, framesType);
        // Which frame is selected?
        for (BotFrame frame: frames){
            if (frame.isSelected()){
                currentFrame = frame;
                currentFrameIndex = frames.indexOf(frame);
            }
            // A workaround to remove unnecessary data from file
            frame.selectedPixels.clear();
        }
        // No selected frame
        currentFrameIndex = 0;
        currentFrame = frames.get(currentFrameIndex);
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
