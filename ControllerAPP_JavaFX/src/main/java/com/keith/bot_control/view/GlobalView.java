package com.keith.bot_control.view;

import com.keith.bot_control.controller.BotControl;
import com.keith.bot_control.BotControlAPP;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GlobalView {

    BotControl botControl = BotControlAPP.getBotControl();

    @FXML
    private Label welcomeText;

}