package com.example.guiscenebuilder;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Organizer extends User implements CRUD {
    private final Wallet wallet;

    public Organizer(String username, String password, Date dateOfBirth) {
        super(username, password, dateOfBirth);
        this.wallet = new Wallet(0.0);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void organizerDashboard(Stage stage, Organizer organizer, Runnable goBack) {
        Label welcome = new Label("Organizer Dashboard");
        Label plz = new Label("Please Select An Option");

        Button dashboardBtn = new Button("Event Dashboard");
        Button viewAttendeesBtn = new Button("View My Attendees");
        Button availableRoomsBtn = new Button("View Available Rooms");
        Button viewBalanceBtn = new Button("View Balance");
        Button statsBtn = new Button("Statistics");
        Button ChatBtn = new Button("Chat");
        Button logoutBtn = new Button("Logout");

        dashboardBtn.setOnAction(e -> eventDashboard(stage, organizer, () -> organizerDashboard(stage, organizer, goBack)));
        viewAttendeesBtn.setOnAction(e -> viewMyAttendees(stage, organizer, () -> organizerDashboard(stage, organizer, goBack)));
        availableRoomsBtn.setOnAction(e -> viewAvailableRooms(stage, () -> organizerDashboard(stage, organizer, goBack)));
        viewBalanceBtn.setOnAction(e -> viewBalance(stage, organizer, () -> organizerDashboard(stage, organizer, goBack)));
        statsBtn.setOnAction(e -> viewStatistics(stage, organizer, () -> organizerDashboard(stage, organizer, goBack)));
        ChatBtn.setOnAction(e -> {
            try {
                OpenOrganizerChat(organizer);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        logoutBtn.setOnAction(e -> goBack.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        HBox welcomeBox = new HBox(welcome);
        welcomeBox.setAlignment(Pos.CENTER);
        grid.add(welcomeBox, 0, 0, 3, 1);

        HBox plzBox = new HBox(plz);
        plzBox.setAlignment(Pos.CENTER);
        grid.add(plzBox, 0, 1, 3, 1);

        grid.add(dashboardBtn, 0, 2);
        grid.add(viewAttendeesBtn, 1, 2);
        grid.add(availableRoomsBtn, 2, 2);
        grid.add(viewBalanceBtn, 0, 3);
        grid.add(statsBtn, 1, 3);
        grid.add(ChatBtn, 2, 3);
        grid.add(logoutBtn, 1, 5);

        Scene scene = new Scene(grid, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Organizer Dashboard");
    }

    public void OpenOrganizerChat(Organizer organizer) throws Exception {
        OrganizerChat.setOrganizer(organizer);
        new OrganizerChat().start(new Stage());
    }

    public void viewMyAttendees(Stage stage, Organizer organizer, Runnable goBack) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        int row = 0;

        Label title = new Label("--- My Event Attendees ---");
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        grid.add(titleBox, 0, row++, 2, 1);

        boolean hasEvents = false;

        for (Event event : Database.getEvents()) {
            if (organizer.equals(event.getOrganizer())) {
                hasEvents = true;
                if (event.getAttendees().isEmpty()) {
                    grid.add(new Label(event.getTitle() + ": No attendees."), 0, row++, 2, 1);
                } else {
                    for (Attendee a : event.getAttendees()) {
                        grid.add(new Label(event.getTitle() + " - " + a.getUsername()), 0, row++, 2, 1);
                    }
                }
            }
        }

        if (!hasEvents) {
            showAlert(Alert.AlertType.INFORMATION, "No Events", "You have no events with attendees.");
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());
        grid.add(backBtn, 0, row);

        Scene scene = new Scene(grid, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }



    public void viewAvailableRooms(Stage stage, Runnable goBack) {
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setVgap(10);
        layout.setHgap(10);

        Label dateLabel = new Label("Date: ");
        DatePicker eventDateDP = new DatePicker();
        Label label = new Label("Enter Time:");
        ComboBox<String> timecb = new ComboBox<>();
        timecb.getItems().addAll("Day", "Night");
        Button checkBtn = new Button("Check");
        GridPane.setColumnSpan(checkBtn, 2);

        VBox roomDisplay = new VBox(5);

        checkBtn.setOnAction(e -> {
            try {
                String time = timecb.getValue();
                LocalDate dateloc = eventDateDP.getValue();
                Date date = Date.from(dateloc.atStartOfDay(ZoneId.systemDefault()).toInstant());
                List<Room> available = Database.getRooms().stream().filter(r -> r.isAvailable(time, date)).toList();
                roomDisplay.getChildren().clear();
                if (available.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Rooms", "No rooms available at " + time);
                    roomDisplay.getChildren().add(new Label("No available rooms at " + time));
                } else {
                    available.forEach(room -> roomDisplay.getChildren().add(new Label(room.toString())));
                }
            } catch (Exception ex) {
                roomDisplay.getChildren().clear();
                roomDisplay.getChildren().add(new Label("Invalid time format."));
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid time format entered! Use HH:mm.");
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());

        layout.add(dateLabel, 0, 0);
        layout.add(label, 0, 1);
        layout.add(eventDateDP, 1, 0);
        layout.add(timecb, 1, 1);
        layout.add(checkBtn, 1, 3);
        layout.add(roomDisplay, 0, 4, 2, 1);
        layout.add(backBtn, 1, 5);

        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }


    public void viewBalance(Stage stage, Organizer organizer, Runnable goBack) {
        GridPane layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setVgap(10);

        Label label = new Label("Wallet Balance: $" + organizer.getWallet().getBalance());
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        if (organizer.getWallet().getBalance() == 0.0) {
            showAlert(Alert.AlertType.INFORMATION, "Zero Balance", "You currently have no funds in your wallet.");
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());

        layout.add(labelBox, 0, 0);
        layout.add(backBtn, 0, 1);

        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }


    public void viewStatistics(Stage stage, Organizer organizer, Runnable goBack) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Total Earnings
        Label totalLabel = new Label("Total Earnings: $" + organizer.wallet.getBalance());
        HBox totalBox = new HBox(totalLabel);
        totalBox.setAlignment(Pos.CENTER);

        // Breakdown Label
        Label breakdown = new Label("Earnings per Event:");
        HBox breakdownBox = new HBox(breakdown);
        breakdownBox.setAlignment(Pos.CENTER);

        // Earnings List
        VBox earningsList = new VBox(5);
        boolean hasEarnings = false;
        earningsList.setAlignment(Pos.CENTER);

        for (Event event : Database.getEvents()) {
            if (organizer.equals(event.getOrganizer())) {
                double earnings = event.getTicketPrice() * event.getAttendees().size();
                earningsList.getChildren().add(new Label(event.getTitle() + ": $" + earnings));
                hasEarnings = true;
            }
        }

        if (!hasEarnings) {
            showAlert(Alert.AlertType.INFORMATION, "No Earnings", "No events found or no attendees joined.");
        }

        // Back Button
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());

        // Add components to layout
        layout.getChildren().addAll(totalBox, breakdownBox, earningsList, backBtn);

        // Create scene and set it on stage
        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void create(Stage stage, Object obj, Runnable goBack) {
        Organizer organizer = (Organizer) obj;
        GridPane layoutpane = new GridPane();
        layoutpane.setPadding(new Insets(20));
        layoutpane.setAlignment(Pos.CENTER);
        Label title = new Label("--- Create Event ---");
        Label timeTitle = new Label("Time: ");
        ComboBox<String> timecb = new ComboBox<>();
        timecb.getItems().addAll("Day", "Night");
        Label dateLabel = new Label("Date: ");
        DatePicker eventDateDP = new DatePicker();
        eventDateDP.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!item.isAfter(LocalDate.now())) { // disables today and past dates
                            setDisable(true);
                            setStyle("-fx-background-color: #EEEEEE;");
                        }
                    }
                };
            }
        });

        ArrayList<Room> available_rooms = new ArrayList<>();
        Button nextbtn = new Button("Next");
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());
        layoutpane.add(title, 0, 0);
        layoutpane.add(timeTitle, 0, 1);
        layoutpane.add(timecb, 1, 1);
        layoutpane.add(dateLabel, 0, 2);
        layoutpane.add(eventDateDP, 1, 2);
        layoutpane.add(nextbtn, 1, 3);
        layoutpane.add(backBtn, 2, 3);
        nextbtn.setOnAction(e -> {
            String time = timecb.getValue();
            LocalDate dateloc = eventDateDP.getValue();
            Date date = Date.from(dateloc.atStartOfDay(ZoneId.systemDefault()).toInstant());

            if (time == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            }
            else {
                for (Room room : Database.getRooms()) {
                    if (room.isAvailable(time, date)) {
                        available_rooms.add(room);
                    }
                }
            }
            if (available_rooms.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No rooms available at the specified time.");
            }
            else {
                GridPane layout = new GridPane();
                layout.setPadding(new Insets(20));
                layout.setAlignment(Pos.CENTER);
                Label titleLabel = new Label("Title: ");
                TextField titleTF = new TextField();
                titleTF.setPromptText("Event Title");
                Label roomLabel = new Label("Room: ");
                ComboBox<Room> roomCB = new ComboBox<>();
                roomCB.getItems().addAll(available_rooms);
                Label eventCategory = new Label("Category: ");
                ComboBox<Category> ctgCB = new ComboBox<>();
                ctgCB.getItems().addAll(Database.getCategories());
                Label eventPrice = new Label("Price: ");
                TextField eventPriceTF = new TextField();
                Button createBtn = new Button("Create");
                layout.add(title, 1, 0);
                layout.add(titleLabel, 0, 1);
                layout.add(titleTF, 1, 1);
                layout.add(roomLabel, 0, 2);
                layout.add(roomCB, 1, 2);
                layout.add(eventCategory, 0, 3);
                layout.add(ctgCB, 1, 3);
                layout.add(eventPrice, 0, 4);
                layout.add(eventPriceTF, 1, 4);
                layout.setHgap(10);
                layout.setVgap(10);
                createBtn.setOnAction(e4 -> {
                    String titleStr = titleTF.getText();
                    String priceStr = eventPriceTF.getText();
                    String dateStr = eventDateDP.getValue().toString();
                    Room room = roomCB.getValue();
                    Category category = ctgCB.getValue();
                    room.addbookedtime(time, date);
                    double price = 0.0;
                    if (titleStr.isEmpty() || priceStr.isEmpty() || dateStr.isEmpty() || room == null || category == null) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                    }
                    else {
                        try {
                            price = Double.parseDouble(priceStr);
                        }
                        catch (NumberFormatException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    Event eventcrt = new Event(room, titleStr, price, category);
                    eventcrt.setOrganizer(organizer);
                    Database.getEvents().add(eventcrt);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Event created successfully.");
                    goBack.run();

                });
                layout.add(createBtn, 1, 6);

                Button backBtn2 = new Button("Back");
                layout.add(backBtn2, 1, 7);
                backBtn2.setOnAction(e2 -> goBack.run());
                Scene scene2 = new Scene(layout, 700, 400);
                scene2.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                stage.setScene(scene2);
                stage.setTitle("Create Event");
                stage.show();
            }

        });
        layoutpane.setHgap(10);
        layoutpane.setVgap(10);
        Scene scene = new Scene(layoutpane, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Create Event");
        stage.show();
        stage.setResizable(false);


    }


    public void read(Stage stage, Object obj, Runnable goBack) {
        Organizer organizer = (Organizer) obj;
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setVgap(10);

        // Title Label
        Label title = new Label("--- Your Organized Events ---");

        // ListView for events
        ListView<String> eventList = new ListView<>();
        boolean hasEvents = false;


        for (Event event : Database.getEvents()) {
            if (event.getOrganizer().equals(organizer)) {
                eventList.getItems().add(event.getTitle());
                hasEvents = true;
            }
        }


        if (!hasEvents) {
            showAlert(Alert.AlertType.INFORMATION, "No Events", "You have not organized any events yet.");
        }


        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());


        HBox titleHBox = new HBox(title);
        titleHBox.setAlignment(Pos.CENTER);
        titleHBox.setSpacing(10);


        VBox eventListVBox = new VBox(10);
        eventListVBox.setAlignment(Pos.CENTER);
        eventListVBox.getChildren().addAll(eventList, backBtn);


        layout.add(titleHBox, 0, 0);
        layout.add(eventListVBox, 0, 1);


        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Your Organized Events");
        stage.show();
    }

    public void update(Stage stage, Object obj, Runnable goBack) {
        boolean hasEvents = false;
        Organizer organizer = (Organizer) obj;
        ArrayList<Event> eventList = new ArrayList<>();

        for (Event event : Database.getEvents()) {
            if (event.getOrganizer().equals(organizer)) {
                eventList.add(event);
                hasEvents = true;
            }
        }

        if (!hasEvents) {
            showAlert(Alert.AlertType.INFORMATION, "No Events", "You have not organized any events yet.");
            return;
        }

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(10);
        layout.setVgap(10);


        Label title = new Label("--- Update Organized Event ---");


        ComboBox<Event> evtcb = new ComboBox<>();
        evtcb.getItems().addAll(eventList);
        evtcb.setPromptText("Select an Event");


        Label eventtle = new Label();
        Label eventroom = new Label();
        Label eventctg = new Label();
        Label eventprc = new Label();


        evtcb.setOnAction(e -> {
            Event selected = evtcb.getSelectionModel().getSelectedItem();
            if (selected != null) {
                eventtle.setText("Title: " + selected.getTitle());
                eventroom.setText("Room: " + selected.getRoom());
                eventctg.setText("Category: " + selected.getCategoryName());
                eventprc.setText("Price: $" + selected.getPrice());
            }
        });


        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            Event selected = evtcb.getSelectionModel().getSelectedItem();
            if (selected != null) {
                GridPane grdpane = new GridPane();
                grdpane.setPadding(new Insets(20));
                grdpane.setAlignment(Pos.CENTER);
                grdpane.setHgap(10);
                grdpane.setVgap(10);


                Label evttlelbl = new Label("Title: ");
                Label evtroomlbl = new Label("Room: ");
                Label evtctglbl = new Label("Category: ");
                Label evtprclbl = new Label("Price: ");
                TextField tletf = new TextField(selected.getTitle());
                ComboBox<Room> roomcb = new ComboBox<>();
                roomcb.getItems().addAll(Database.getRooms());
                roomcb.setValue(selected.getRoom());
                ComboBox<Category> ctgcb = new ComboBox<>();
                ctgcb.getItems().addAll(Database.getCategories());
                ctgcb.setValue(selected.getCategory());
                TextField prctf = new TextField(String.valueOf(selected.getPrice()));


                grdpane.add(evttlelbl, 0, 0);
                grdpane.add(tletf, 1, 0);
                grdpane.add(evtroomlbl, 0, 1);
                grdpane.add(roomcb, 1, 1);
                grdpane.add(evtctglbl, 0, 2);
                grdpane.add(ctgcb, 1, 2);
                grdpane.add(evtprclbl, 0, 3);
                grdpane.add(prctf, 1, 3);


                Button updateBtn = new Button("Update");
                updateBtn.setOnAction(e2 -> {
                    selected.setTitle(tletf.getText());
                    selected.setRoom(roomcb.getValue());
                    selected.setCategory(ctgcb.getValue());
                    selected.setPrice(Double.parseDouble(prctf.getText()));

                    eventprc.setText("Price: $" + selected.getPrice());
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Event updated successfully.");
                    evtcb.getItems().remove(selected);
                    evtcb.getItems().add(selected);
                    evtcb.getSelectionModel().select(selected);
                });


                Button backBtn2 = new Button("Back");
                backBtn2.setOnAction(e2 -> goBack.run());


                grdpane.add(updateBtn, 1, 4);
                grdpane.add(backBtn2, 2, 4);


                Scene scene2 = new Scene(grdpane, 700, 400);
                scene2.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                stage.setScene(scene2);
                stage.setTitle("Update Organized Event");
                stage.show();
            }
        });


        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack.run());


        HBox titleHBox = new HBox(10, title);
        titleHBox.setAlignment(Pos.CENTER);

        VBox eventDetailsVBox = new VBox(10, evtcb, eventtle, eventroom, eventctg, eventprc);
        eventDetailsVBox.setAlignment(Pos.CENTER);

        HBox buttonHBox = new HBox(10, updateButton, backButton);
        buttonHBox.setAlignment(Pos.CENTER);


        layout.add(titleHBox, 0, 0, 2, 1);
        layout.add(eventDetailsVBox, 0, 1, 2, 1);
        layout.add(buttonHBox, 0, 2, 2, 1);


        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Update Event");
        stage.show();
    }
    public void delete(Stage stage, Object obj, Runnable goBack) {
        Organizer organizer = (Organizer) obj;
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(10);
        layout.setVgap(10);


        Label title = new Label("--- Delete Organized Event ---");


        ListView<String> eventList = new ListView<>();
        boolean hasEvents = false;

        for (Event event : Database.getEvents()) {
            if (event.getOrganizer().equals(organizer)) {
                eventList.getItems().add(event.getTitle());
                hasEvents = true;
            }
        }


        if (!hasEvents) {
            showAlert(Alert.AlertType.INFORMATION, "No Events", "You have not organized any events yet.");
            return;
        }


        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            String selected = eventList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                for (Event event : Database.getEvents()) {
                    if (event.getTitle().equals(selected)) {
                        Database.getEvents().remove(event);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Event deleted successfully.");
                        goBack.run();
                        return;
                    }
                }
            }
        });


        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());


        HBox titleHBox = new HBox(10, title);
        titleHBox.setAlignment(Pos.CENTER);

        VBox eventListVBox = new VBox(10, eventList);
        eventListVBox.setAlignment(Pos.CENTER);

        HBox buttonHBox = new HBox(10, deleteBtn, backBtn);
        buttonHBox.setAlignment(Pos.CENTER);


        layout.add(titleHBox, 0, 0, 2, 1);
        layout.add(eventListVBox, 0, 1, 2, 1);
        layout.add(buttonHBox, 0, 2, 2, 1);


        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void eventDashboard(Stage stage, Organizer organizer, Runnable goBack) {
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(10);
        layout.setVgap(10);


        Label label = new Label("--- Event Dashboard ---");
        HBox titleHBox = new HBox(10, label);
        titleHBox.setAlignment(Pos.CENTER);


        Button createBtn = new Button("Create new event");
        Button updateBtn = new Button("Update an existing event");
        Button viewBtn = new Button("View my events");
        Button deleteBtn = new Button("Delete an existing event");
        Button backBtn = new Button("Back");


        VBox buttonVBox = new VBox(10, createBtn, updateBtn, viewBtn, deleteBtn);
        buttonVBox.setAlignment(Pos.CENTER);

        // Back button in HBox
        HBox backHBox = new HBox(10, backBtn);
        backHBox.setAlignment(Pos.CENTER);


        createBtn.setOnAction(e -> create(stage, organizer, () -> eventDashboard(stage, organizer, goBack)));
        updateBtn.setOnAction(e -> update(stage, organizer, () -> eventDashboard(stage, organizer, goBack)));
        viewBtn.setOnAction(e -> read(stage, organizer, () -> eventDashboard(stage, organizer, goBack)));
        deleteBtn.setOnAction(e -> delete(stage, organizer, () -> eventDashboard(stage, organizer, goBack)));
        backBtn.setOnAction(e -> goBack.run());


        layout.add(titleHBox, 0, 0, 2, 1);
        layout.add(buttonVBox, 0, 1, 2, 1);
        layout.add(backHBox, 0, 2, 2, 1);


        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Event Dashboard");
        stage.show();
    }


    @Override
    public String toString () {
        return "Username = " + username + ", dateOfBirth = " + dateOfBirth;
    }
}

