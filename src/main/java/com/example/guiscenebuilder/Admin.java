package com.example.guiscenebuilder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Admin extends User implements CRUD {
    private String role;
    private int workingHours;

    public Admin(String username, String password, Date dateOfBirth, String role, int workingHours) {
        super(username, password, dateOfBirth);
        this.role = role;
        this.workingHours = workingHours;
    }

    public void adminDashboard(Stage stage,Admin admin , Runnable goback) {
        Label welcome = new Label("              Admin Dashboard");
        Label plz = new Label("          Please select An Option");
        Button showrooms = new Button("Show all rooms");
        Button showevents = new Button(" Show all events");
        Button showatt = new Button("Show all attendees");
        Button showorg = new Button("Show all organizers");
        Button addaroom = new Button("Add a room");
        Button cat = new Button("Category Dashboard");
        Button chat = new Button("Chat");
        Button logout = new Button("Logout");
        showrooms.setOnAction(e-> viewAllRooms(stage, () -> adminDashboard(stage,admin, goback)));
        showevents.setOnAction(e-> viewAllEvents(stage, () -> adminDashboard(stage,admin, goback)));
        showatt.setOnAction(e -> viewAllAttendees(stage, () -> adminDashboard(stage,admin, goback)));
        showorg.setOnAction(e -> viewAllOrganizers(stage, () -> adminDashboard(stage,admin, goback)));
        addaroom.setOnAction(e -> addRoom(stage,() -> adminDashboard(stage,admin, goback)));
        cat.setOnAction(e-> categoryDashboard(stage,() -> adminDashboard(stage,admin, goback)));
        chat.setOnAction(e-> {
            try {
                OpenAdminChat(admin);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        logout.setOnAction(e-> goback.run());
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(welcome, 1, 0, 2, 1);
        grid.add(plz, 1, 1, 2, 1);
        grid.add(showrooms, 0, 2);
        grid.add(showevents, 0, 3);
        grid.add(showatt, 1, 2);
        grid.add(showorg, 1, 3);
        grid.add(addaroom, 2, 2);
        grid.add(cat, 2, 3);
        grid.add(chat, 1, 4);
        grid.add(logout, 1, 6);

        Scene scene = new Scene(new StackPane(grid), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);

    }
    public void OpenAdminChat(Admin admin) throws Exception {
        AdminChat.setAdmin(admin);
        new AdminChat().start(new Stage());
    }
    public void viewAllRooms(Stage stage , Runnable goback) {
        Label allrooms = new Label("                             All Rooms");
        ListView<String> roomListView = new ListView<>();
        for (Room r : Database.getRooms()) {
            roomListView.getItems().add(r.toString());
        }
        Button back = new Button("Back");
        back.setOnAction(e -> goback.run());
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER);
        pane.add(allrooms,0,0);
        pane.add(roomListView,0,1);
        pane.add(back,0,3);
        Scene scene = new Scene(new StackPane(pane),600,500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);


    }
    public void create(Stage stage,Object obj, Runnable goback)
    {
        Label title = new Label("Create New Category");
        Label lblName = new Label("Category Name:");
        TextField txtName = new TextField();
        txtName.setPromptText("Enter Category Name");
        Button btnCreate = new Button("Create");
        Button btnBack = new Button("Back");
        btnCreate.setOnAction(e-> {
            String name= txtName.getText().trim();
            if (name.isEmpty())
            {
                showAlert(Alert.AlertType.ERROR,"Error", "Please enter a category name.");
                return;
            }
            Category category= new Category(name);
            Database.getCategories().add(category);
            showAlert(Alert.AlertType.INFORMATION,"Success", "Category created successfully");
            goback.run();
        });
        btnBack.setOnAction(e-> goback.run());
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title, 0, 0);
        grid.add(lblName, 0, 1);
        grid.add(txtName, 1, 1);
        grid.add(btnCreate, 0, 2);
        grid.add(btnBack, 0, 4);

        Scene scene = new Scene(new StackPane(grid), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }
    public void update(Stage stage,Object obj, Runnable goBack) {
        Label title = new Label("Update Category");
        Label lblSelect = new Label("Select Category:");
        ListView<String> categoryListView = new ListView<>();
        for (Category c : Database.getCategories()) {
            categoryListView.getItems().add(c.getName());
        }
        Label lblNewName = new Label("New Name:");
        TextField txtNewName = new TextField();
        txtNewName.setPromptText("Enter new category name");
        Button btnUpdate = new Button("Update");
        Button btnBack = new Button("Back");

        btnUpdate.setOnAction(e -> {
            String selected = categoryListView.getSelectionModel().getSelectedItem();
            String newName = txtNewName.getText().trim();
            if (selected == null || newName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a category and enter a new name.");
                return;
            }
            boolean found = false;
            for (Category category : Database.getCategories()) {
                if (category.getName().equals(selected)) {
                    category.setName(newName);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Category updated successfully!");
                    found = true;
                    goBack.run();
                    break;
                }
            }
            if (!found) {
                showAlert(Alert.AlertType.ERROR, "Error", "Category not found!");
            }
        });

        btnBack.setOnAction(e -> goBack.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title, 1, 0);
        grid.add(lblSelect, 0, 1);
        grid.add(categoryListView, 1, 1);
        grid.add(lblNewName, 0, 2);
        grid.add(txtNewName, 1, 2);
        grid.add(btnUpdate, 0, 4);
        grid.add(btnBack, 1, 4);

        Scene scene = new Scene(new StackPane(grid), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void delete(Stage stage,Object obj, Runnable goBack) {
        Label title = new Label("Delete Category");
        Label lblSelect = new Label("Select Category:");
        ListView<String> categoryListView = new ListView<>();
        for (Category c : Database.getCategories()) {
            categoryListView.getItems().add(c.getName());
        }
        Button btnDelete = new Button("Delete");
        Button btnBack = new Button("Back");

        btnDelete.setOnAction(e -> {
            String selected = categoryListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a category.");
                return;
            }
            boolean found = false;
            for (Category category : Database.getCategories()) {
                if (category.getName().equalsIgnoreCase(selected)) {
                    Database.getCategories().remove(category);
                    Category.noOfCategories--;
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted successfully!");
                    found = true;
                    goBack.run();
                    break;
                }
            }
            if (!found) {
                showAlert(Alert.AlertType.ERROR, "Error", "Category not found!");
            }
        });

        btnBack.setOnAction(e -> goBack.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title, 0, 0);
        grid.add(lblSelect,0,1);
        grid.add(categoryListView, 1, 1);
        grid.add(btnDelete, 0, 3);
        grid.add(btnBack, 1, 3);

        Scene scene = new Scene(new StackPane(grid), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void read(Stage stage,Object obj, Runnable goBack) {
        Label title = new Label("Existing Categories");
        ListView<String> categoryListView = new ListView<>();
        int i = 1;
        for (Category category : Database.getCategories()) {
            categoryListView.getItems().add(i + ") " + category.toString());
            i++;
        }
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title, 0, 0);
        grid.add(categoryListView, 0, 1);
        grid.add(btnBack, 0, 3);

        Scene scene = new Scene(new StackPane(grid), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void viewAllEvents(Stage stage, Runnable goBack) {
        Label title = new Label("--- All Events ---");
        ListView<String> eventListView = new ListView<>();
        for (Event e : Database.getEvents()) {
            eventListView.getItems().add(e.toString());
        }
        Button back = new Button("Back");
        back.setOnAction(e -> goBack.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title,0,0);
        grid.add(eventListView,0,1);
        grid.add(back,0,3);

        Scene scene = new Scene(new StackPane(grid), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }
    public void viewAllAttendees(Stage stage, Runnable goback) {
        Label title = new Label("---All Attendees---");
        ListView<String> attendeeList = new ListView<>();
        for (Attendee a : Database.getAttendees()) {
            attendeeList.getItems().add(a.toString());
        }
        Button back = new Button("Back");
        back.setOnAction(e -> goback.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title,0,0);
        grid.add(attendeeList,0,1);
        grid.add(back,0,3);

        Scene scene = new Scene(new StackPane(grid), 600,500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }
    public void viewAllOrganizers(Stage stage, Runnable goback)
    {
        Label title = new Label("---All Organizers---");
        ListView <String> allorg = new ListView<>();
        for(Organizer o : Database.getOrganizers())
        {
            allorg.getItems().add(o.toString());
        }
        Button back = new Button("Back");
        back.setOnAction(e -> goback.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title,0,0);
        grid.add(allorg,0,1);
        grid.add(back,0,3);

        Scene scene = new Scene(new StackPane(grid),600,500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void categoryDashboard(Stage stage, Runnable goBack)
    {
        Label title = new Label("--- Category Dashboard ---");
        Button btnCreate = new Button("Create New Category");
        Button btnUpdate = new Button("Update Existing Category");
        Button btnRead = new Button("Read Existing Categories");
        Button btnDelete = new Button("Delete Existing Category");
        Button btnTotal = new Button("Get Total Number of Categories");
        Button btnBack = new Button("Back");

        btnCreate.setOnAction(e -> create(stage, this,() -> categoryDashboard(stage, goBack)));
        btnUpdate.setOnAction(e -> update(stage,this, () -> categoryDashboard(stage, goBack)));
        btnRead.setOnAction(e -> read(stage,this, () -> categoryDashboard(stage, goBack)));
        btnDelete.setOnAction(e -> delete(stage,this, () -> categoryDashboard(stage, goBack)));
        btnTotal.setOnAction(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Total Categories", "Total Categories: " + Category.getNoOfCategories());
        });
        btnBack.setOnAction(e -> goBack.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title, 1, 0);
        grid.add(btnCreate, 0, 1);
        grid.add(btnUpdate, 1, 1);
        grid.add(btnDelete, 2, 1);
        grid.add(btnRead, 0, 2);
        grid.add(btnTotal,2,2);
        grid.add(btnBack, 1, 2);

        Scene scene = new Scene(new StackPane(grid), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }



    public void addRoom(Stage stage, Runnable goback)
    {
        Label title = new Label("Add a room");
        Label roomname = new Label("Room name: ");
        TextField txtName= new TextField();
        Button btnAdd= new Button("Add Room");
        Button back = new Button("Back");
        btnAdd.setOnAction(e -> {
            String roomName = txtName.getText().trim();
            if (roomName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            try {
                Room newRoom = new Room(roomName);
                Database.getRooms().add(newRoom);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room added successfully");
                goback.run();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid time format. Use HH:mm (e.g., 14:30).");
            }
        });

        back.setOnAction(e -> goback.run());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(title, 1, 0);
        grid.add(roomname, 0, 1);
        grid.add(txtName, 1, 1);
        grid.add(btnAdd, 0, 2);
        grid.add(back, 1, 2);

        Scene scene = new Scene(new StackPane(grid), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    @Override
    public String toString()
    {
        return "\nUsername:  " + username + "  ,Date of Birth:  " + dateOfBirth + "  ,Role:  " + role + "  ,Working Hours:  " + workingHours ;
    }
}