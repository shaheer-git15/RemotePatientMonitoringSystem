package UI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import DatabaseConnector.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class LoginPage extends Application {

    private VBox mainContainer;
    private VBox loginForm;
    private VBox signUpForm;
    private Label titleLabel;
    private VBox welcomeSection;

    @Override
    public void start(Stage primaryStage) {
        // Create the main container with HBox for split layout
        HBox splitContainer = new HBox();
        splitContainer.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, #b2dfdb, 12, 0.2, 2, 0);"); // Updated to match sidebar colors
        splitContainer.setAlignment(Pos.CENTER);

        // Create welcome section (left side)
        welcomeSection = createWelcomeSection();
        welcomeSection.setPrefWidth(600);
        welcomeSection.setStyle("-fx-background-color: transparent; -fx-padding: 40;");

        // Create forms container (right side)
        StackPane formsContainer = new StackPane();
        formsContainer.setStyle("-fx-background-color: transparent;");
        formsContainer.setPrefWidth(600);
        formsContainer.setAlignment(Pos.CENTER);
        formsContainer.setPadding(new Insets(60, 0, 60, 0));

        // Create both forms
        loginForm = createLoginForm();
        signUpForm = createSignUpForm();
        signUpForm.setVisible(false);

        // Add forms to container
        formsContainer.getChildren().addAll(loginForm, signUpForm);

        // Add both sections to split container
        splitContainer.getChildren().addAll(welcomeSection, formsContainer);

        // Create large, low-opacity medical symbols for the background
        Label plusSign = new Label("+");
        plusSign.setFont(Font.font("Arial", FontWeight.BOLD, 400));
        plusSign.setTextFill(Color.web("#ffffff"));
        plusSign.setOpacity(0.08);
        plusSign.setAlignment(Pos.CENTER_LEFT);
        plusSign.setTranslateX(-350);
        plusSign.setTranslateY(-100);

        Label heartSign = new Label("\u2665"); // Unicode heart
        heartSign.setFont(Font.font("Arial", FontWeight.BOLD, 300));
        heartSign.setTextFill(Color.web("#ffffff"));
        heartSign.setOpacity(0.07);
        heartSign.setAlignment(Pos.BOTTOM_RIGHT);
        heartSign.setTranslateX(350);
        heartSign.setTranslateY(200);

        Label caduceusSign = new Label("\u2695"); // Unicode medical symbol
        caduceusSign.setFont(Font.font("Arial", FontWeight.BOLD, 200));
        caduceusSign.setTextFill(Color.web("#ffffff"));
        caduceusSign.setOpacity(0.09);
        caduceusSign.setAlignment(Pos.TOP_RIGHT);
        caduceusSign.setTranslateX(350);
        caduceusSign.setTranslateY(-250);

        // Use a StackPane as the root to overlay the medical symbols and the splitContainer
        StackPane root = new StackPane();
        root.getChildren().addAll(plusSign, heartSign, caduceusSign, splitContainer);

        // Create scene
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Remote Patient Monitoring System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createWelcomeSection() {
        VBox welcomeBox = new VBox(20);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setPadding(new Insets(40));

        // Welcome title
        Label welcomeTitle = new Label("Welcome to\nRemote Patient\nMonitoring System");
        welcomeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        welcomeTitle.setTextFill(Color.WHITE);
        welcomeTitle.setTextAlignment(TextAlignment.CENTER);

        // Add a decorative line
        Separator separator = new Separator();
        separator.setMaxWidth(200);
        separator.setStyle("-fx-background-color: white;");

        // Add a simple tagline
        Label tagline = new Label("Healthcare at Your Fingertips");
        tagline.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        tagline.setTextFill(Color.WHITE);
        tagline.setTextAlignment(TextAlignment.CENTER);

        welcomeBox.getChildren().addAll(welcomeTitle, separator, tagline);
        return welcomeBox;
    }

    private VBox createLoginForm() {
        VBox loginForm = new VBox(12); // More spacing for larger elements
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setMaxWidth(480); // Increased width
        loginForm.setMinHeight(400); // Increased min height
        loginForm.setMaxHeight(450); // Increased max height
        loginForm.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-padding: 32 40; -fx-background-radius: 12; " +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.18), 18, 0, 0, 0); " +
                          "-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 1; -fx-border-radius: 12;");

        // Form title
        Label formTitle = new Label("Login");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Larger font
        formTitle.setTextFill(Color.web("#2c3e50"));

        // Email field with label and prompt text
        VBox emailContainer = new VBox(3);
        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("Arial", 15)); // Larger font
        emailLabel.setTextFill(Color.web("#666666"));
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle("-fx-padding: 10; -fx-font-size: 16; -fx-background-radius: 7; " +
                           "-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; " +
                           "-fx-border-width: 1.5; -fx-border-radius: 7;");
        emailContainer.getChildren().addAll(emailLabel, emailField);

        // Password field with label and prompt text
        VBox passwordContainer = new VBox(3);
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Arial", 15)); // Larger font
        passwordLabel.setTextFill(Color.web("#666666"));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 10; -fx-font-size: 16; -fx-background-radius: 7; " +
                             "-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; " +
                             "-fx-border-width: 1.5; -fx-border-radius: 7;");
        passwordContainer.getChildren().addAll(passwordLabel, passwordField);

        // Role selection with label and prompt text
        VBox roleContainer = new VBox(3);
        Label roleLabel = new Label("Role");
        roleLabel.setFont(Font.font("Arial", 15)); // Larger font
        roleLabel.setTextFill(Color.web("#666666"));
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Patient", "Doctor", "Administrator");
        roleComboBox.setPromptText("Select your role");
        roleComboBox.setStyle("-fx-padding: 10; -fx-font-size: 16; -fx-background-radius: 7; " +
                            "-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1.5; -fx-border-radius: 7;");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleContainer.getChildren().addAll(roleLabel, roleComboBox);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 18; " +
                           "-fx-padding: 12 0; -fx-background-radius: 7; -fx-cursor: hand;");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                                                              "-fx-font-size: 18; -fx-padding: 12 0; -fx-background-radius: 7; " +
                                                              "-fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                                                             "-fx-font-size: 18; -fx-padding: 12 0; -fx-background-radius: 7; " +
                                                             "-fx-cursor: hand;"));

        // Sign up link
        Hyperlink signUpLink = new Hyperlink("New Patient? Sign up here");
        signUpLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14; -fx-cursor: hand;");
        signUpLink.setOnMouseEntered(e -> signUpLink.setStyle("-fx-text-fill: #2980b9; -fx-font-size: 14; -fx-cursor: hand;"));
        signUpLink.setOnMouseExited(e -> signUpLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14; -fx-cursor: hand;"));

        // Add components to login form
        loginForm.getChildren().addAll(
            formTitle,
            emailContainer,
            passwordContainer,
            roleContainer,
            loginButton,
            signUpLink
        );

        // Login button action
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();

            if (email.isEmpty() || password.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
                return;
            }

            if (validateLogin(email, password, role)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");
                if (role.equals("Patient")) {
                    // Open the Patient Dashboard
                    new PatientDashboard((Stage) ((Button) e.getSource()).getScene().getWindow(), getUserIdFromEmail(email));
                } else if (role.equals("Doctor")) {
                    // Open the Doctor Dashboard
                    new DoctorDashboard((Stage) ((Button) e.getSource()).getScene().getWindow(), getUserIdFromEmail(email));
                } else if (role.equals("Administrator")) {
                    // Open the Admin Dashboard
                    new AdminDashboard((Stage) ((Button) e.getSource()).getScene().getWindow(), getUserIdFromEmail(email));
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid credentials");
            }
        });

        // Sign up link action
        signUpLink.setOnAction(e -> {
            loginForm.setVisible(false);
            signUpForm.setVisible(true);
        });

        return loginForm;
    }

    private VBox createSignUpForm() {
        VBox signUpForm = new VBox(10); // Set spacing to 10
        signUpForm.setAlignment(Pos.CENTER);
        signUpForm.setMaxWidth(400);
        signUpForm.setMinHeight(600);
        signUpForm.setMaxHeight(700);
        signUpForm.setStyle("-fx-background-color: white; -fx-padding: 18 24; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Form title
        Label formTitle = new Label("Create New Account");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        formTitle.setTextFill(Color.web("#2c3e50"));

        // User ID
        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        userIdField.setStyle("-fx-padding: 6; -fx-font-size: 13; -fx-background-radius: 5;");

        // Name
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setStyle("-fx-padding: 6; -fx-font-size: 13; -fx-background-radius: 5;");

        // Age
        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        ageField.setStyle("-fx-padding: 6; -fx-font-size: 13; -fx-background-radius: 5;");

        // Gender
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setPromptText("Select Gender");
        genderComboBox.setStyle("-fx-padding: 6; -fx-font-size: 13; -fx-background-radius: 5;");
        genderComboBox.setMaxWidth(Double.MAX_VALUE);

        // Address
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.setStyle("-fx-padding: 6; -fx-font-size: 13; -fx-background-radius: 5;");

        // Email
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-padding: 6; -fx-font-size: 13; -fx-background-radius: 5;");

        // Password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-padding: 6; -fx-font-size: 13; -fx-background-radius: 5;");

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 8 0; -fx-background-radius: 5;");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        // Back to login link
        Hyperlink backToLoginLink = new Hyperlink("Already have an account? Login here");
        backToLoginLink.setStyle("-fx-text-fill: #3498db; -fx-font-size: 12;");

        // Add components to form
        signUpForm.getChildren().addAll(
            formTitle,
            userIdField,
            nameField,
            ageField,
            genderComboBox,
            addressField,
            emailField,
            passwordField,
            registerButton,
            backToLoginLink
        );

        // Register button action
        registerButton.setOnAction(e -> {
            if (validateAndRegister(
                userIdField.getText(),
                nameField.getText(),
                ageField.getText(),
                genderComboBox.getValue(),
                addressField.getText(),
                emailField.getText(),
                passwordField.getText()
            )) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! You can now login.");
                // Clear all fields
                userIdField.clear();
                nameField.clear();
                ageField.clear();
                genderComboBox.setValue(null);
                addressField.clear();
                emailField.clear();
                passwordField.clear();
                // Switch back to login form
                signUpForm.setVisible(false);
                loginForm.setVisible(true);
            }
        });

        // Back to login link action
        backToLoginLink.setOnAction(e -> {
            signUpForm.setVisible(false);
            loginForm.setVisible(true);
        });

        // Ensure no field is auto-focused when the form appears
        javafx.application.Platform.runLater(() -> signUpForm.requestFocus());

        return signUpForm;
    }

    private boolean validateLogin(String email, String password, String role) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if user exists with given credentials
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getUserIdFromEmail(String email) {
        String query = "SELECT user_id FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("user_id");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if user not found
    }

    private boolean validateAndRegister(String userId, String name, String age, String gender, 
                                      String address, String email, String password) {
        // Validate input
        if (userId.isEmpty() || name.isEmpty() || age.isEmpty() || gender == null || 
            address.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
            return false;
        }

        // Validate age
        try {
            int ageNum = Integer.parseInt(age);
            if (ageNum <= 0 || ageNum > 120) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid age");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Age must be a number");
            return false;
        }

        // Insert into database
        String query = "INSERT INTO users (user_id, name, age, gender, address, email, password, role) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, 'Patient')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, name);
            pstmt.setInt(3, Integer.parseInt(age));
            pstmt.setString(4, gender);
            pstmt.setString(5, address);
            pstmt.setString(6, email);
            pstmt.setString(7, password);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert(Alert.AlertType.ERROR, "Error", "User ID or Email already exists");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Registration failed: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        // Add this line to ensure JavaFX modules are loaded
        System.setProperty("javafx.verbose", "true");
        launch(args);
    }
} 