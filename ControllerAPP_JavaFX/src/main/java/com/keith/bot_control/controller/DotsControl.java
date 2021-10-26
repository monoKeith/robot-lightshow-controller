package com.keith.bot_control.controller;

import com.keith.bot_control.view.DotsView;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DotsControl {

    DotsView view;

    public DotsControl(){
        this.view = null;
    }

    public void setView(DotsView view) {
        this.view = view;
    }

    public void updateView(){
        GraphicsContext gc = view.getCanvas().getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,10,10);
        System.out.println("Update canvas");
    }


}
