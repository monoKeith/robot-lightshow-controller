module com.keith.controllerapp_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.keith.controllerapp_javafx to javafx.fxml;
    exports com.keith.controllerapp_javafx;
}