package com.example.guiscenebuilder;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class MainApp extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Database.initializeDummyData();
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Event Management System");
        showMainDashboard();
        primaryStage.show();
    }

    private void showMainDashboard() {
        Label lblWelcome = new Label(" Welcome To Event\nManagement System");
        lblWelcome.setStyle("-fx-font-size: 20px;");

        Button btnRegister = new Button("Register");
        Button btnLogin = new Button("Login");
        Button btnExit = new Button("Exit");

        btnRegister.setOnAction(e -> showRegisterScene());
        btnLogin.setOnAction(e -> showLoginScene());
        btnExit.setOnAction(e -> primaryStage.close());

        GridPane gridPane = new GridPane();
        gridPane.setVgap(15);
        gridPane.setHgap(15);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(lblWelcome, 1, 1, 2, 1);
        gridPane.add(btnRegister, 0, 2);
        gridPane.add(btnLogin, 1, 2);
        gridPane.add(btnExit, 2, 2);

        StackPane root = new StackPane();
        root.getChildren().addAll( gridPane);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showRegisterScene() {
        Label lblWelcome = new Label("        Register As: ");
        lblWelcome.setStyle("-fx-font-size: 20px;");

        Button btnBack = new Button("Back");
        Button btnAdmin = new Button("Admin");
        Button btnOrganizer = new Button("Organizer");
        Button btnAttendee = new Button("Attendee");

        btnAdmin.setOnAction(e -> User.adminRegistration(primaryStage, this::showRegisterScene, this::showMainDashboard));
        btnOrganizer.setOnAction(e -> User.organizerRegistration(primaryStage, this::showRegisterScene, this::showMainDashboard));
        btnAttendee.setOnAction(e -> User.attendeeRegistration(primaryStage, this::showRegisterScene, this::showMainDashboard));
        btnBack.setOnAction(e -> showMainDashboard());

        GridPane gridPane = new GridPane();
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(lblWelcome, 0, 0, 2, 1);
        gridPane.add(btnAdmin, 0, 1);
        gridPane.add(btnAttendee, 0, 2);
        gridPane.add(btnOrganizer, 0, 3);
        gridPane.add(btnBack, 0, 5);

        Scene scene = new Scene(gridPane, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showLoginScene() {
        Label lblWelcome = new Label("        Login as: ");
        lblWelcome.setStyle("-fx-font-size: 20px;");

        Button btnBack = new Button("Back");
        Button btnadmin = new Button("Admin");
        Button btnorganizer = new Button("Organizer");
        Button btnattendee = new Button("Attendee");

        btnadmin.setOnAction(e -> User.adminLogin(primaryStage, this::showLoginScene, this::showMainDashboard));
        btnorganizer.setOnAction(e -> User.organizerLogin(primaryStage, this::showLoginScene, this::showMainDashboard));
        btnattendee.setOnAction(e -> User.attendeeLogin(primaryStage, this::showLoginScene, this::showMainDashboard));
        btnBack.setOnAction(e -> showMainDashboard());

        GridPane gridPane = new GridPane();
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(lblWelcome, 0, 0, 2, 1);
        gridPane.add(btnadmin, 0, 1);
        gridPane.add(btnattendee, 0, 2);
        gridPane.add(btnorganizer, 0, 3);
        gridPane.add(btnBack, 0, 5);

        Scene scene = new Scene(gridPane, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }
}
