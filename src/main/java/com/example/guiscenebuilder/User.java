package com.example.guiscenebuilder;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Date;


public class User {
    protected String username;
    protected String password;
    protected Date dateOfBirth;

    public String getPassword() {
        return password;
    }

    public User(String username, String password, Date dateOfBirth) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public static boolean registerCheck(String username, int choice) {
        switch (choice) {
            case 1:
                for (Admin a : Database.getAdmins()) {
                    if (a.getUsername().equalsIgnoreCase(username)) {
                        return true;
                    }
                }
                break;
            case 2:
                for (Organizer o : Database.getOrganizers()) {
                    if (o.getUsername().equalsIgnoreCase(username)) {
                        return true;
                    }
                }
                break;
            case 3:
                for (Attendee at : Database.getAttendees()) {
                    if (at.getUsername().equalsIgnoreCase(username)) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }


    public static void adminRegistration(Stage primaryStage, Runnable goBack, Runnable goHome) {
        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        Label lblDOB = new Label("Date of Birth:");
        DatePicker datePicker = new DatePicker();

        Label lblRole = new Label("Role:");
        TextField txtRole = new TextField();
        Label lblHours = new Label("Working Hours:");
        TextField txtHours = new TextField();
        Label lblStatus = new Label();

        txtUsername.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                boolean taken = User.registerCheck(newVal.trim(), 1);
                lblStatus.setText(taken ? "Username already exists." : "Username is available.");
                lblStatus.setStyle("-fx-text-fill: " + (taken ? "red" : "rgba(208, 255, 240, 0.8)") + ";");
            } else {
                lblStatus.setText("");
            }
        });

        Button btnRegister = new Button("Register");
        btnRegister.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            String role = txtRole.getText().trim();
            String hoursText = txtHours.getText().trim();
            LocalDate dob = datePicker.getValue();


            if (username.isEmpty() || password.isEmpty() || role.isEmpty() || hoursText.isEmpty() || dob == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }

            int hours;
            try {
                hours = Integer.parseInt(hoursText);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Working hours must be numeric.");
                return;
            }

            if (User.registerCheck(username, 1)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
                return;
            }

            Admin admin = new Admin(username, password, java.sql.Date.valueOf(dob), role, hours);
            Database.getAdmins().add(admin);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Admin registered successfully!");
            goHome.run();
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());

        VBox layout = new VBox(10, lblUsername, txtUsername, lblPassword, txtPassword,
                lblDOB, datePicker, lblRole, txtRole, lblHours, txtHours,
                lblStatus, btnRegister, btnBack);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(new StackPane(layout), 600, 500);
        scene.getStylesheets().add(User.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void organizerRegistration(Stage primaryStage, Runnable goBack, Runnable goHome) {
        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");

        Label lblDOB = new Label("Date of Birth:");
        DatePicker datePicker = new DatePicker();


        Label lblStatus = new Label();

        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                boolean usernameTaken = User.registerCheck(newValue.trim(), 2);
                if (usernameTaken) {
                    lblStatus.setText("Username already exists.");
                    lblStatus.setStyle("-fx-text-fill: red;");
                } else {
                    lblStatus.setText("Username is available.");
                    lblStatus.setStyle("-fx-text-fill: green;");
                }
            } else {
                lblStatus.setText("");
            }
        });

        Button btnRegister = new Button("Register");
        btnRegister.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            LocalDate localDate = datePicker.getValue();

            if (username.isEmpty() || password.isEmpty() || localDate == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }

            boolean usernameTaken = User.registerCheck(username, 2);
            if (usernameTaken) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
                return;
            }

            Date dob = java.sql.Date.valueOf(localDate);
            Organizer org = new Organizer(username, password, dob);
            Database.getOrganizers().add(org);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Organizer registered successfully!");
            goHome.run();
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());

        VBox vbox = new VBox(10,
                lblUsername, txtUsername,
                lblPassword, txtPassword,
                lblDOB, datePicker,
                lblStatus,
                btnRegister, btnBack
        );
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(300);

        Scene scene = new Scene(new StackPane(vbox), 600, 500);
        scene.getStylesheets().add(User.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void attendeeRegistration(Stage primaryStage, Runnable goBack, Runnable goHome) {
        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");

        Label lblDOB = new Label("Date of Birth:");
        DatePicker datePicker = new DatePicker();

        Label lblBalance = new Label("Initial Balance:");
        TextField txtBalance = new TextField();
        txtBalance.setPromptText("Enter initial balance");

        Label lblAddress = new Label("Address:");
        TextField txtAddress = new TextField();
        txtAddress.setPromptText("Enter your address");

        Label lblGender = new Label("Gender:");
        ComboBox<Gender> cbGender = new ComboBox<>();
        cbGender.getItems().addAll(Gender.values());
        cbGender.setPromptText("Select gender");

        Label lblInterests = new Label("Interests:");
        TextField txtInterests = new TextField();
        txtInterests.setPromptText("Enter your interests");

        Label lblStatus = new Label();

        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                boolean usernameTaken = User.registerCheck(newValue.trim(), 3);
                if (usernameTaken) {
                    lblStatus.setText("Username already exists.");
                    lblStatus.setStyle("-fx-text-fill: red;");
                } else {
                    lblStatus.setText("Username is available.");
                    lblStatus.setStyle("-fx-text-fill: green;");
                }
            } else {
                lblStatus.setText("");
            }
        });

        Button btnRegister = new Button("Register");
        btnRegister.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            String address = txtAddress.getText().trim();
            String interest = txtInterests.getText().trim();
            String balanceText = txtBalance.getText().trim();
            Gender gender = cbGender.getValue();
            LocalDate localDate = datePicker.getValue();

            if (username.isEmpty() || password.isEmpty() || address.isEmpty() || interest.isEmpty()
                    || balanceText.isEmpty() || gender == null || localDate == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }

            double balance;
            try {
                balance = Double.parseDouble(balanceText);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Balance must be a number.");
                return;
            }

            boolean usernameTaken = User.registerCheck(username, 3);
            if (usernameTaken) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
                return;
            }

            Date dob = java.sql.Date.valueOf(localDate);
            Attendee newAttendee = new Attendee(username, password, dob, balance, address, gender, interest);
            Database.getAttendees().add(newAttendee);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Attendee registered successfully!");
            goHome.run();
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());

        VBox vbox = new VBox(10,
                lblUsername, txtUsername,
                lblPassword, txtPassword,
                lblDOB, datePicker,
                lblBalance, txtBalance,
                lblAddress, txtAddress,
                lblGender, cbGender,
                lblInterests, txtInterests,
                lblStatus,
                btnRegister, btnBack
        );
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(300);

        Scene scene = new Scene(new StackPane(vbox), 600, 600);
        scene.getStylesheets().add(User.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void showAlert(Alert.AlertType alertType, String title, String msg) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void adminLogin(Stage primaryStage, Runnable goBack, Runnable goHome) {
        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            Admin loggedInAdmin = null;
            for (Admin a : Database.getAdmins()) {
                if (a.getUsername().equals(username) && a.getPassword().equals(password)) {
                    loggedInAdmin = a;
                    break;
                }
            }

            if (loggedInAdmin == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");
                loggedInAdmin.adminDashboard(primaryStage,loggedInAdmin,goHome);
            }
        });
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());

        VBox layout = new VBox(10, lblUsername, txtUsername, lblPassword, txtPassword, btnLogin, btnBack);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(new StackPane(layout), 600, 500);
        scene.getStylesheets().add(User.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void organizerLogin(Stage primaryStage, Runnable goBack, Runnable goHome) {
        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            Organizer loggedInOrganizer = null;
            for (Organizer o : Database.getOrganizers()) {
                if (o.getUsername().equals(username) && o.getPassword().equals(password)) {
                    loggedInOrganizer = o;
                    break;
                }
            }

            if (loggedInOrganizer == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");
                loggedInOrganizer.organizerDashboard(primaryStage, loggedInOrganizer,goHome);
            }
        });
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());

        VBox layout = new VBox(10, lblUsername, txtUsername, lblPassword, txtPassword, btnLogin, btnBack);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(new StackPane(layout), 600, 500);
        scene.getStylesheets().add(User.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void attendeeLogin(Stage primaryStage, Runnable goBack, Runnable goHome) {
        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            Attendee loggedInAttendee = null;
            for (Attendee at : Database.getAttendees()) {
                if (at.getUsername().equals(username) && at.getPassword().equals(password)) {
                    loggedInAttendee = at;
                    break;
                }
            }

            if (loggedInAttendee == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");
                loggedInAttendee.attendeeDashboard(primaryStage, loggedInAttendee,goHome);
            }
        });
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> goBack.run());

        VBox layout = new VBox(10, lblUsername, txtUsername, lblPassword, txtPassword, btnLogin, btnBack);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(new StackPane(layout), 600, 500);
        scene.getStylesheets().add(User.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }
}