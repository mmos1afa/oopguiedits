module com.example.guiscenebuilder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.guiscenebuilder to javafx.fxml;
    exports com.example.guiscenebuilder;
}