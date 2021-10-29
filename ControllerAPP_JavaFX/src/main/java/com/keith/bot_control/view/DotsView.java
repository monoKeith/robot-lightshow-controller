package com.keith.bot_control.view;

import com.keith.bot_control.BotControlAPP;
import com.keith.bot_control.controller.DotsCanvasControl;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

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

    public Canvas getCanvas(){
        return canvas;
    }


}
