package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.PropertiesControl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.util.Map;
import java.util.UUID;

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

    @FXML
    protected CheckBox displayLightBotID;


    /* Connected Bots */

    @FXML
    protected TableView<Map.Entry<Integer, UUID>> botTable;

    @FXML
    protected TableColumn<Map.Entry<Integer, UUID>, String> pixelIdColumn;

    @FXML
    protected TableColumn<Map.Entry<Integer, UUID>, String> botUUIDColumn;




    public PropertiesView(){
    }

    @FXML
    public void initialize(){
        control.setView(this);
        control.refreshView();
        // Initialize connected LightBot table properties
        botUUIDColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().toString()));
        pixelIdColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey().toString()));
    }

    /* BotPixel Properties */

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

    public void displayLightBotIdUpdate(){
        control.showPixelIdUpdate(displayLightBotID.isSelected());
    }

    @FXML
    public void duplicateCurrentFrame(){
        control.duplicateCurrentFrame();
    }

    @FXML
    public void deleteCurrentFrame(){
        control.deleteCurrentFrame();
    }

    @FXML
    public void moveToLeft(){
        control.rearrangeSelectedFrame(true);
    }

    @FXML
    public void moveToRight(){
        control.rearrangeSelectedFrame(false);
    }

    /* UUID map */

    public void updateConnectedBots(Map<Integer, UUID> pixelIdMap){
        ObservableList<Map.Entry<Integer, UUID>> list = FXCollections.observableList(pixelIdMap.entrySet().stream().toList());
        botTable.setItems(list);
    }

}
