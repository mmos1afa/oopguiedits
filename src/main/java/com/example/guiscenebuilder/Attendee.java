package com.example.guiscenebuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Attendee extends User{
    private Gender gender;
    private double balance;
    private String address;
    private Wallet wallet;
    private List<Event>registeredevents;
    private String interests;

    public Attendee(String username, String password, Date dateOfBirth, double intialBalance, String address, Gender gender, String interests) {
        super(username, password, dateOfBirth);
        this.gender = gender;
        this.balance= intialBalance;
        this.address=address;
        this.wallet=new Wallet(intialBalance);
        this.interests=interests;
        this.registeredevents = new ArrayList<>();
    }

    public void attendeeDashboard(Stage stage, Attendee att, Runnable goBack) {
        Label welcome = new Label("Attendee Dashboard");
        Label plz = new Label("Please select an option");
        Button btt = new Button("View Profile");
        Button btt2 = new Button("View All Events");
        Button bttt = new Button("Search For Events");
        Button b = new Button("View My Events");
        Button s = new Button("Buy Ticket");
        Button r = new Button("View Balance");
        Button h = new Button("Add Balance");
        Button z = new Button("Logout");

        btt.setOnAction(e -> att.viewProfile(stage, () -> att.attendeeDashboard(stage, att, goBack)));
        btt2.setOnAction(e -> att.viewAllEvents(stage,()->att.attendeeDashboard(stage,att,goBack)));
        bttt.setOnAction(e -> att.searchEvents(stage,()->att.attendeeDashboard(stage,att,goBack)));
        b.setOnAction(e -> att.viewMyEvents(stage, () -> att.attendeeDashboard(stage, att, goBack)));
        s.setOnAction(e -> att.registerEvent(stage, () -> att.attendeeDashboard(stage, att, goBack)));
        r.setOnAction(e -> att.viewBalance(stage,()->att.attendeeDashboard(stage,att,goBack)));
        h.setOnAction(e -> att.AddBalance(stage,()->att.attendeeDashboard(stage,att,goBack)));
        z.setOnAction(e -> goBack.run());
        VBox v = new VBox(10, welcome, plz);
        v.setAlignment(Pos.CENTER);

        VBox v1 = new VBox(10, btt2, bttt, b, s);
        v1.setAlignment(Pos.CENTER);
        v1.setPadding(new Insets(10));

        VBox v2 = new VBox(10, r, h, btt, z);
        v2.setAlignment(Pos.CENTER);
        v2.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(30));

        grid.add(v, 1, 1, 2, 1);
        grid.add(v1, 1, 2);
        grid.add(v2, 2, 2);

        Scene scene = new Scene(grid, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Attendee Dashboard");
        stage.setScene(scene);
    }

    public List<Event> getRegisteredEvents(){
        return registeredevents;
    }

    public void viewMyEvents(Stage stage, Runnable goBack) {
        Label lblTitle = new Label("--- Your Registered Events ---");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        if (this.getRegisteredEvents().isEmpty()) {
            Label lblNoEvents = new Label("No Events Registered!");
            layout.getChildren().addAll(lblTitle, lblNoEvents);
        } else {
            ListView<String> eventListView = new ListView<>();
            for (Event e : this.getRegisteredEvents()) {
                eventListView.getItems().add(e.toString());
            }

            layout.getChildren().addAll(lblTitle, eventListView);
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> {
            goBack.run();
        });

        layout.getChildren().add(btnBack);

        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void registerEvent(Stage stage, Runnable goBack) {
        Label titleLabel = new Label("---- Register for an Event ----");

        ComboBox<Event> eventComboBox = new ComboBox<>();
        eventComboBox.getItems().addAll(Database.getEvents());
        eventComboBox.setPromptText("Select an Event");

        Label eventDetails = new Label();
        eventComboBox.setOnAction(e -> {
            Event selected = eventComboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                eventDetails.setText("Price: $" + selected.getPrice());
            }
        });

        Label statusLabel = new Label();

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            Event selected = eventComboBox.getValue();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an event.");
                return;
            }

            if (wallet.getBalance() < selected.getPrice()) {
                showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "Your wallet balance is too low.");
            } else {
                double newBalance = wallet.withDraw(selected.getPrice());
                selected.addAttendee(this);
                selected.getOrganizer().getWallet().deposit(selected.getPrice());
                this.registeredevents.add(selected);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Registered successfully! New Balance: $" + newBalance);
            }

        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack.run());

        VBox layout = new VBox(15, titleLabel, eventComboBox, eventDetails, registerButton, statusLabel, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }




    public void viewAllEvents(Stage stage, Runnable goBack) {
        Label lblTitle = new Label("--- All Events ---");
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        if (Database.getEvents().isEmpty()) {
            Label lblNoEvents = new Label("No events available.");
            layout.getChildren().addAll(lblTitle, lblNoEvents);
        } else {
            ListView<String> eventListView = new ListView<>();
            for (Event e : Database.getEvents()) {
                eventListView.getItems().add(e.toString());
            }
            layout.getChildren().addAll(lblTitle, eventListView);
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());
        layout.getChildren().add(btnBack);

        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }


    public void viewProfile(Stage stage, Runnable goBack) {
        Label lblTitle = new Label(" Profile Details ");

        Label lblUsername = new Label("Username: " + this.username);
        Label lblPassword = new Label("Password: " + this.password);
        Label lblDOB = new Label("Date of Birth: " + this.dateOfBirth);
        Label lblGender = new Label("Gender: " + this.gender);

        Label lblEvents = new Label("Registered Events:");
        TextArea eventsArea = new TextArea();
        eventsArea.setEditable(false);
        eventsArea.setPrefHeight(150);

        if (getRegisteredEvents().isEmpty()) {
            eventsArea.setText("No events registered.");
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            for (Event e : getRegisteredEvents()) {
                sb.append(e.toString()).append("\n");
            }
            eventsArea.setText(sb.toString());
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());


        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(15);

        grid.add(lblTitle, 0, 0, 2, 1);
        GridPane.setHalignment(lblTitle, HPos.CENTER);
        grid.add(lblUsername, 0, 1);
        grid.add(lblDOB, 0, 2);

        grid.add(lblPassword, 1, 1);
        grid.add(lblGender, 1, 2);

        grid.add(lblEvents, 0, 3, 2, 1);
        grid.add(eventsArea, 0, 4, 2, 1);
        grid.add(btnBack, 0, 5, 2, 1);
        GridPane.setHalignment(btnBack, HPos.CENTER);

        Scene scene = new Scene(grid, 500, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Attendee Profile");
    }


    public void viewBalance(Stage stage,Runnable goBack){
        Label label=new Label("View Wallet Balance");
        Label label1=new Label("Balance $:"+wallet.getBalance());
        Button button=new Button("Back");
        button.setOnAction(e->goBack.run());
        VBox layout = new VBox(20, label, label1, button);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);


    }

    public void AddBalance(Stage stage,Runnable goBack){
        Label label=new Label("Add Balance");
        Label label3= new Label("Enter amount to Deposit");
        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Amount");
        Label result=new Label();
        Button button=new Button("Deposit");
        button.setOnAction(e->{
            try{
                double amount=Double.parseDouble(txtAmount.getText());
                double newBalance=wallet.deposit(amount);
                showAlert(Alert.AlertType.INFORMATION, "Deposit Successful", "Your new balance is $" + newBalance);

            }
            catch(NumberFormatException ex){
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
            }
        });
        Button button2=new Button("Back");
        button2.setOnAction(e->goBack.run());
        VBox layout=new VBox(15,label,label3,txtAmount,result,button,button2);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);


    }
    @Override
    public String toString() {
        return "Username: " + username + "  ,Date of Birth: " + dateOfBirth + "  ,Gender: " + gender + "  ,Balance: " + balance + "  ,Address: " + address + "  ,Interests: " + interests;
    }

    public void searchEvents(Stage stage,Runnable goBack){

        Label label1=new Label("----Search Events----");
        Button button=new Button("Search By Name");
        Button button1=new Button("Search By Category");
        Button button2=new Button("Back");
        VBox layout=new VBox(20,label1,button,button1,button2);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        button2.setOnAction(e->goBack.run());
        button.setOnAction(e->searchByName(stage,goBack));
        button1.setOnAction(e->searchByCategory(stage,goBack));
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }
    private void searchByName(Stage stage,Runnable goBack){
        Label label=new Label("Enter your Event Name:");
        TextField textField=new TextField();
        Button bttn =new Button("Search");
        Label lblResults = new Label();
        bttn.setOnAction(e->{
            String name=textField.getText();
            StringBuilder results=new StringBuilder();
            Database.getEvents().stream()
                    .filter(ev -> ev.getEventName().equalsIgnoreCase(name))
                    .forEach(ev -> results.append(ev.toString()).append("\n"));
            if (results.length() > 0) {
                lblResults.setText(results.toString());
            } else {
                lblResults.setText("No events found.");
            }


        });
        Button btt=new Button("Back");
        btt.setOnAction(ex-> searchEvents(stage, goBack));
        VBox layout=new VBox(15,label,textField,bttn,lblResults,btt);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }
    private void searchByCategory(Stage stage,Runnable goBack){
        Label label=new Label("Search by Category");
        TextField textField=new TextField();
        Button button=new Button("Search");
        Label lblResults=new Label();
        button.setOnAction(c->{
            String category=textField.getText();
            StringBuilder result=new StringBuilder();
            Database.getEvents().stream()
                    .filter(ev -> ev.getCategoryName().equalsIgnoreCase(category))
                    .forEach(ev -> result.append(ev.toString()).append("\n"));
            if (result.length() > 0) {
                lblResults.setText(result.toString());
            } else {
                lblResults.setText("No events found.");
            }
        });
        Button button1=new Button("Back");
        button1.setOnAction(cm->searchEvents(stage,goBack));
        VBox layout=new VBox(15,label,textField,button,lblResults,button1);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }
}
