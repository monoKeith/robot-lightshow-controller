package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.view.PropertiesView;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Set;

public class PropertiesControl {

    private BotControl control;
    private PropertiesView view;

    public PropertiesControl(BotControl botControl){
        this.control = botControl;
        view = null;
    }

    public void setView(PropertiesView view) {
        this.view = view;
    }

    // If only one pixel selected, returns the selected pixel, otherwise null
    private BotPixel singleSelectedPixel(){
        Set<BotPixel> selectedPixels = control.getSelectedPixels();
        if (selectedPixels.size() == 1){
            return (BotPixel) selectedPixels.toArray()[0];
        }
        return null;
    }

    /* Handlers */

    public void pixelColorModified(Color color){
        for (BotPixel pixel: control.getSelectedPixels()){
            pixel.setColor(color);
        }
        // Refresh canvas
        control.notifyPropertiesUpdate();
    }

    public void locationUpdate(Point2D newLocation, boolean physicalLocation){
        BotPixel selectedPixel = singleSelectedPixel();
        if (selectedPixel == null) return;
        if (physicalLocation){
            selectedPixel.setPhysicalLocation(newLocation);
        } else {
            selectedPixel.setPixelLocation(newLocation);
        }
        // Refresh
        refreshView();
        control.notifyPropertiesUpdate();
    }

    /* Update */

    public void refreshView(){
        Platform.runLater(() -> {
            Set<BotPixel> selectedPixels = control.getSelectedPixels();

            if (selectedPixels.isEmpty()){
                view.setBotPixelPaneEnable(false);
                view.displayMultipleSelectionWarning(false);

            } else if (selectedPixels.size() == 1){
                view.setBotPixelPaneEnable(true);
                view.displayMultipleSelectionWarning(false);
                view.enableLocationProperties(true);

                BotPixel pixel = singleSelectedPixel();
                // Color
                view.botPixelPresetColor(pixel.getColor());
                // Location
                view.setPhysicalLocation(pixel.getPhysicalLocation());
                view.setCanvasLocation(pixel.getPixelLocation());

            } else {
                // Multiple BotPixel selected
                view.enableLocationProperties(false);
                view.displayMultipleSelectionWarning(true);

            }
        });
    }


    /* Logging */

    private void log(String msg){
        System.out.printf("[%s] %s%n", getClass().getSimpleName() , msg);
    }

}
