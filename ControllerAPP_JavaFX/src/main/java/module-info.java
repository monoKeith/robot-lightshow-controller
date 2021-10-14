module com.keith.controllerapp_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.keith.bot_control to javafx.fxml;
    exports com.keith.bot_control;
}