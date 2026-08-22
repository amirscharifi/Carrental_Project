module com.example.carrental {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires spring.boot.autoconfigure;
    requires spring.boot;


    opens com.example.carrental to javafx.fxml;
    exports com.example.carrental;
}