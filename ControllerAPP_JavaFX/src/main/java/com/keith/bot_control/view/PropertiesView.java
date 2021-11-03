package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.PropertiesControl;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PropertiesView {

    private PropertiesControl control = BotControlAPP.getBotControl().getPropertiesControl();


    /* Bot Pixel properties*/

    @FXML
    protected Label multipleSelectionWarning;

    @FXML
    protected GridPane colorPropertiesPane, locationPropertiesPane;

    @FXML
    protected ColorPicker botPixelColorPicker;

    @FXML
    protected TextField physicsX, physicsY, canvasX, canvasY;

    @FXML
    protected Button applyButtonPhysical, applyButtonCanvas;


    /* Frame properties */

    @FXML
    protected TextField frameName;


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
        colorPropertiesPane.setDisable(!enable);
        locationPropertiesPane.setDisable(!enable);
    }

    private Point2D parseToPoint(String textX, String textY){
        double x, y;
        try {
            x = Double.parseDouble(textX);
        } catch (NumberFormatException e){
            x = 0;
        }
        try {
            y = Double.parseDouble(textY);
        } catch (NumberFormatException e){
            y = 0;
        }
        return new Point2D(x, y);
    }

    @FXML
    protected void locationTextChangePhysics(Event e){
        applyButtonPhysical.setDisable(false);
        applyButtonPhysical.setText("Apply");
    }

    @FXML
    protected void locationTextChangeCanvas(Event e){
        applyButtonCanvas.setDisable(false);
        applyButtonCanvas.setText("Apply");
    }

    @FXML
    protected void locationUpdatePhysics(Event e){
        control.locationUpdate(parseToPoint(physicsX.getText(), physicsY.getText()), true);
    }

    @FXML
    protected void locationUpdateCanvas(Event e){
        control.locationUpdate(parseToPoint(canvasX.getText(), canvasY.getText()), false);
    }

    public void setPhysicalLocation(Point2D location){
        physicsX.setText(String.format("%.2f", location.getX()));
        physicsY.setText(String.format("%.2f", location.getY()));
        applyButtonPhysical.setDisable(true);
        applyButtonPhysical.setText("original");
    }

    public void setCanvasLocation(Point2D location){
        canvasX.setText(String.format("%.1f", location.getX()));
        canvasY.setText(String.format("%.1f", location.getY()));
        applyButtonCanvas.setDisable(true);
        applyButtonCanvas.setText("original");
    }

    public void enableLocationProperties(boolean enable){
        locationPropertiesPane.setDisable(!enable);
    }

    public void displayMultipleSelectionWarning(boolean enable){
        multipleSelectionWarning.setVisible(enable);
    }


    /* Frame Properties */

    @FXML
    protected void frameNameChange(Event e){
        control.frameNameUpdate(frameName.getText());
    }

    public void setFrameName(String name){
        frameName.setText(name);
    }

}
