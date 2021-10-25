module com.keith.controllerapp_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.eclipse.paho.client.mqttv3;


    opens com.keith.bot_control to javafx.fxml;
    exports com.keith.bot_control;
    exports com.keith.bot_control.view;
    opens com.keith.bot_control.view to javafx.fxml;
    exports com.keith.bot_control.controller;
    opens com.keith.bot_control.controller to javafx.fxml;
    exports com.keith.bot_control.model;
    opens com.keith.bot_control.model to javafx.fxml;
}