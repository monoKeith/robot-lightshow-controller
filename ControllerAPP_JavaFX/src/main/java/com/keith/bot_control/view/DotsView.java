
package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.DotsCanvasControl;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public class DotsView {

    DotsCanvasControl control = BotControlAPP.getBotControl().getDotsControl();

    @FXML
    private Canvas canvas;

    public DotsView(){

    }

    @FXML
    public void initialize(){
        control.setView(this);
        control.refreshView();
    }

    @FXML
    public void mousePress(MouseEvent event){
        double x = event.getX();
        double y = event.getY();
        control.mousePress(new Point2D(x, y));
    }

    public Canvas getCanvas(){
        return canvas;
    }

    public void clearCanvas(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getHeight(), canvas.getHeight());
    }

}
