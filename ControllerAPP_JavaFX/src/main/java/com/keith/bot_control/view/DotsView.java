package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.DotsCanvasControl;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public class DotsView {

    DotsCanvasControl control = BotControlAPP.getBotControl().getDotsControl();

    @FXML
    private Canvas canvas;

    private Point2D dragStartingPoint;
    private Boolean dragging;

    public DotsView(){
        dragging = false;
    }

    @FXML
    public void initialize(){
        control.setView(this);
        control.refreshView();
    }

    /* Mouse Events */

    public Point2D point(MouseEvent event){
        double x = event.getX();
        double y = event.getY();
        return new Point2D(x, y);
    }

    public Dimension2D delta(MouseEvent event){
        double deltaX = event.getX() - dragStartingPoint.getX();
        double deltaY = event.getY() - dragStartingPoint.getY();
        return new Dimension2D(deltaX, deltaY);
    }

    @FXML
    public synchronized void mouseDragUpdate(MouseEvent event){
        if (!dragging){
            dragStartingPoint = point(event);
            dragging = true;
        }
        control.mouseDragging(delta(event));
    }

    @FXML
    public void mouseRelease(MouseEvent event){
        if (dragging) {
            // drag released
            control.mouseDragReleased(delta(event));
        } else {
            // mouse clicked
            control.mouseClick(point(event));
        }
        dragging = false;
    }

    /* Getter and Setter */
    public Canvas getCanvas(){
        return canvas;
    }

    public void clearCanvas(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getHeight(), canvas.getHeight());
    }

}
