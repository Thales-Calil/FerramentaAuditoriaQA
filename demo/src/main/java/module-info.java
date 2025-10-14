module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.view to javafx.fxml;

    exports com.example.demo;
    exports com.example.demo.controller;
    exports com.example.demo.model;
    exports com.example.demo.view;

    opens com.example.demo.model to javafx.base;
}