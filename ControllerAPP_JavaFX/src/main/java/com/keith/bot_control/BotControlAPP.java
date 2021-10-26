package com.keith.bot_control;

import com.keith.bot_control.controller.BotControl;
import com.keith.bot_control.controller.ConnectionControl;
import com.keith.bot_control.controller.DotsControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BotControlAPP extends Application {

    private static BotControl botControl;
    private static boolean initialized = false;

    public BotControlAPP() {
        botControl = new BotControl();
        initialized = true;
    }

    public static BotControl getBotControl() {
        return initialized ? botControl : null;
    }

    public static DotsControl getDotsControl() {
        return initialized ? botControl.getDotsControl() : null;
    }

    public static ConnectionControl getConnectionControl() {
        return initialized ? botControl.getConnectionControl() : null;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BotControlAPP.class.getResource("global-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 945);
        stage.setTitle("WeBots - Robot Light Show Controller");
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnCloseRequest(windowEvent -> {
            botControl.terminate();
        });

        // Start application
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}