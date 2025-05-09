package com.example.guiscenebuilder;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class OrganizerChat extends Application {
    private static Organizer organizer;

    public static void setOrganizer(Organizer o) {
        organizer = o;
    }
    private PrintWriter writer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);

        TextField inputField = new TextField();
        inputField.setPromptText("Type a message...");
        Button sendButton = new Button("Send");


        VBox root = new VBox(10, chatArea ,inputField,sendButton);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Organizer Chat");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 1616);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                this.writer = out;

                String line;
                while ((line = reader.readLine()) != null) {
                    String msg = "Admin: " + line;
                    Platform.runLater(() -> chatArea.appendText(msg + "\n"));
                }

            } catch (IOException e) {
                Platform.runLater(() -> chatArea.appendText("Could not connect to Admin.\n"));
            }
        }).start();

        sendButton.setOnAction(e -> {
            String msg = inputField.getText();
            if (!msg.isEmpty() && writer != null) {
                writer.println(msg);
                chatArea.appendText("Organizer: " + msg + "\n");
                inputField.clear();
            }
        });
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

    }


    public static void main(String[] args) {
        launch(args);
    }
}
