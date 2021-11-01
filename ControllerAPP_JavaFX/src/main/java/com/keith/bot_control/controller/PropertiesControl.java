package com.keith.bot_control.controller;

import com.keith.bot_control.model.BotPixel;
import com.keith.bot_control.view.PropertiesView;
import javafx.application.Platform;
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

    public void pixelColorModified(Color color){
        Set<BotPixel> selectedPixels = control.getSelectedPixels();
        if (selectedPixels.size() == 1){
            BotPixel pixel = (BotPixel) selectedPixels.toArray()[0];
            pixel.setColor(color);
        }
        // Refresh canvas
        control.pixelPropertiesUpdate();
    }

    public void refreshView(){
        Platform.runLater(() -> {
            Set<BotPixel> selectedPixels = control.getSelectedPixels();

            if (selectedPixels.isEmpty()){
                view.setBotPixelPaneEnable(false);

            } else if (selectedPixels.size() == 1){
                view.setBotPixelPaneEnable(true);
                BotPixel pixel = (BotPixel) selectedPixels.toArray()[0];
                view.botPixelPresetColor(pixel.getColor());

            } else {
                // Multiple botPixel selected

            }

        });
    }

}
