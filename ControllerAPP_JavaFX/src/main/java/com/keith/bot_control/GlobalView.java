package com.keith.bot_control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GlobalView {

    BotControl botControl = BotControlAPP.getBotControl();

    @FXML
    private Label welcomeText;

}