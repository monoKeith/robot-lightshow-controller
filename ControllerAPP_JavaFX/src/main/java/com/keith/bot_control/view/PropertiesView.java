package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.PropertiesControl;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PropertiesView {

    PropertiesControl control = BotControlAPP.getBotControl().getPropertiesControl();

    @FXML
    protected Pane botPixelPropertiesPane;

    @FXML
    protected ColorPicker botPixelColorPicker;

    public PropertiesView(){

    }

    @FXML
    public void initialize(){
        control.setView(this);
        control.refreshView();
    }

    /* BotPixel Properties*/

    @FXML
    protected void pixelColorChanged(){
        control.pixelColorModified(botPixelColorPicker.getValue());
    }

    public void botPixelPresetColor(Color color){
        botPixelColorPicker.setValue(color);
    }

    public void setBotPixelPaneEnable(boolean enable){
        botPixelPropertiesPane.setDisable(!enable);
    }

    /* Other ... TODO */

}
