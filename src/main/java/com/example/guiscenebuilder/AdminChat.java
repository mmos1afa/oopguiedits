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

public class AdminChat extends Application {
    private static Admin admin;

    public static void setAdmin(Admin a) {
        admin = a;
    }
    private PrintWriter writer;
    @Override
    public void start(Stage primaryStage) throws Exception {

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);

        TextField inputField = new TextField();
        inputField.setPromptText("Type a message...");
        Button sendButton = new Button("Send");

        VBox root = new VBox(10, chatArea, inputField, sendButton);
        root.setAlignment(Pos.CENTER);


        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Admin Chat");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(1616);
                 Socket socket = serverSocket.accept();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                this.writer = out;

                String line;
                while ((line = reader.readLine()) != null) {
                    String msg = "Organizer: " + line;
                    Platform.runLater(() -> chatArea.appendText(msg + "\n"));
                }

            } catch (IOException e) {
                Platform.runLater(() -> chatArea.appendText("Connection closed or failed.\n"));
            }
        }).start();

        sendButton.setOnAction(e -> {
            String msg = inputField.getText();
            if (!msg.isEmpty() && writer != null) {
                writer.println(msg);
                chatArea.appendText("Admin: " + msg + "\n");
                inputField.clear();
            }
        });
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

    }

    public static void main(String[] args) {
        launch(args);
    }
}