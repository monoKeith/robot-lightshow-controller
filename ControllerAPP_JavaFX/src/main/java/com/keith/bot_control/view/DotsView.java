package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.DotsControl;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

public class DotsView {

    DotsControl control = BotControlAPP.getDotsControl();

    @FXML
    private Canvas canvas;

    public DotsView(){
        control.setView(this);
    }

    @FXML
    public void initialize(){
        control.refreshView();
    }

    public Canvas getCanvas(){
        return canvas;
    }


}
