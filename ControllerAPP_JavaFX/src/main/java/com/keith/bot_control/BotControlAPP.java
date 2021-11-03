package com.keith.bot_control;

import com.keith.bot_control.controller.BotControl;
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

    // View objects are spawned by javafx, they need static methods to be able to get objects created in main thread.
    public static BotControl getBotControl() {
        return initialized ? botControl : null;
    }

    // Load a FXML file
    public static FXMLLoader loadResource(String filename) {
        return new FXMLLoader(BotControlAPP.class.getResource(filename));
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = loadResource("global-view.fxml");
        Scene scene = new Scene(fxmlLoader.load(), 1440, 925);
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