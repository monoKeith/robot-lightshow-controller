module com.keith.controllerapp_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.eclipse.paho.client.mqttv3;


    opens com.keith.bot_control to javafx.fxml;
    exports com.keith.bot_control;
}