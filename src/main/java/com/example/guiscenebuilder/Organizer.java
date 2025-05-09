package com.example.guiscenebuilder;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        ChatBtn.setOnAction(e-> {
            try {
                OpenOrganizerChat(organizer);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        logoutBtn.setOnAction(e -> goBack.run());
        VBox v = new VBox();
        v.getChildren().addAll(welcome,plz);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);


        grid.add(v,0,0);
        grid.add(dashboardBtn,0,1);
        grid.add(viewAttendeesBtn,1,1);
        grid.add(availableRoomsBtn,2,1);
        grid.add(viewBalanceBtn,0,2);
        grid.add(statsBtn,1,2);
        grid.add(ChatBtn,2,2);
        grid.add(logoutBtn,1,4);



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
        GridPane.setColumnSpan(title, 2);
        grid.add(title, 0, row++);

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

        layout.add(label, 0, 0);
        layout.add(dateLabel, 0, 1);
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
        if (organizer.getWallet().getBalance() == 0.0) {
            showAlert(Alert.AlertType.INFORMATION, "Zero Balance", "You currently have no funds in your wallet.");
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());

        layout.add(label, 0, 0);
        layout.add(backBtn, 0, 1);

        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void viewStatistics(Stage stage, Organizer organizer, Runnable goBack) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label totalLabel = new Label("Total Earnings: $" + organizer.wallet.getBalance());
        Label breakdown = new Label("Earnings per Event:");

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

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack.run());

        layout.getChildren().addAll(totalLabel, breakdown, earningsList, backBtn);
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

        Label title = new Label("--- Your Organized Events ---");
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

        layout.add(title, 0, 0);
        layout.add(eventList, 0, 1);
        layout.add(backBtn, 0, 2);

        Scene scene = new Scene(layout, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
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

        Label eventprc = new Label();
        Label eventtle = new Label();
        Label eventctg = new Label();
        Label eventroom = new Label();

        evtcb.setOnAction(e -> {
            Event selected = evtcb.getSelectionModel().getSelectedItem();
            if (selected != null) {
                eventtle.setText("Title: " + selected.getTitle());
                eventroom.setText("Room: " + selected.getRoom());
                eventctg.setText("Category: " + selected.getCategoryName());
                eventprc.setText("Price: $" + selected.getPrice());
            }
        });

        Label statusLabel = new Label();
        Button updateButton = new Button("Update");

        updateButton.setOnAction(e -> {
            Event selected = evtcb.getSelectionModel().getSelectedItem();
            if (selected != null) {
                GridPane grdpane = new GridPane();

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

                grdpane.setHgap(10);
                grdpane.setVgap(10);
                grdpane.setPadding(new Insets(20));
                grdpane.setAlignment(Pos.CENTER);

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

        layout.add(title, 0, 0, 2, 1);
        layout.add(evtcb, 0, 1, 2, 1);
        layout.add(eventtle, 0, 2);
        layout.add(eventroom, 0, 3);
        layout.add(eventctg, 0, 4);
        layout.add(eventprc, 0, 5);
        layout.add(updateButton, 0, 6);
        layout.add(backButton, 1, 6);

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

        layout.add(title, 0, 0, 2, 1);
        layout.add(eventList, 0, 1, 2, 1);
        layout.add(deleteBtn, 0, 2);
        layout.add(backBtn, 1, 2);

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
        HBox h = new HBox();
        h.getChildren().addAll(label);
        h.setAlignment(Pos.CENTER);
        Button createBtn = new Button("Create new event");
        Button updateBtn = new Button("Update an existing event");
        Button viewBtn = new Button("View my events");
        Button deleteBtn = new Button("Delete an existing event");
        Button backBtn = new Button("Back");
        HBox hbox = new HBox();
        hbox.getChildren().add(backBtn);
        hbox.setAlignment(Pos.CENTER);

        createBtn.setOnAction(e -> create(stage, organizer, () -> eventDashboard(stage, organizer, goBack)));
        updateBtn.setOnAction(e -> update(stage, organizer, () -> eventDashboard(stage, organizer, goBack)));
        viewBtn.setOnAction(e -> read(stage, organizer, () -> eventDashboard(stage, organizer, goBack)));
        deleteBtn.setOnAction(e -> delete(stage, organizer, () -> eventDashboard(stage, organizer, goBack)));
        backBtn.setOnAction(e -> goBack.run());

        layout.add(h, 0, 0, 2, 1);
        layout.add(createBtn, 0, 1);
        layout.add(updateBtn, 1, 1);
        layout.add(viewBtn, 0, 2);
        layout.add(deleteBtn, 1, 2);
        layout.add(hbox, 0, 3,2,1);

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

